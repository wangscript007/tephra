package org.lpw.tephra.cache.redis;

import org.lpw.tephra.bean.ContextRefreshedListener;
import org.lpw.tephra.cache.Handler;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Serializer;
import org.lpw.tephra.util.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.inject.Inject;

/**
 * @author lpw
 */
@Component("tephra.cache.redis.handler")
public class HandlerImpl implements Handler, ContextRefreshedListener {
    @Inject
    private Serializer serializer;
    @Inject
    private Validator validator;
    @Inject
    private Logger logger;
    @Value("${tephra.cache.redis.host:}")
    private String host;
    @Value("${tephra.cache.redis.port:6379}")
    private int port;
    @Value("${tephra.cache.redis.password:}")
    private String password;
    @Value("${tephra.cache.redis.max-total:500}")
    private int total;
    @Value("${tephra.cache.redis.max-idle:5}")
    private int idle;
    @Value("${tephra.cache.redis.max-wait:500}")
    private long wait;
    private JedisPool pool;

    @Override
    public String getName() {
        return "redis";
    }

    @Override
    public void put(String key, Object value, boolean resident) {
        try (Jedis jedis = pool.getResource()) {
            jedis.set(key.getBytes(), serializer.serialize(value));
        } catch (Throwable throwable) {
            logger.warn(throwable, "推送Redis缓存数据[{}:{}:{}]时发生异常！", key, value, resident);
        }
    }

    @Override
    public <T> T get(String key) {
        return get(key, false);
    }

    @Override
    public <T> T remove(String key) {
        return get(key, true);
    }

    private <T> T get(String key, boolean remove) {
        Jedis jedis = pool.getResource();
        byte[] k = key.getBytes();
        byte[] bytes = jedis.get(k);
        if (validator.isEmpty(bytes)) {
            jedis.close();

            return null;
        }

        if (remove)
            jedis.del(k);
        T t = unserialize(jedis, k, bytes);
        jedis.close();

        return t;
    }

    private <T> T unserialize(Jedis jedis, byte[] key, byte[] bytes) {
        jedis.close();
        try {
            return serializer.unserialize(bytes);
        } catch (Throwable throwable) {
            jedis.del(key);

            return null;
        }
    }

    @Override
    public int getContextRefreshedSort() {
        return 6;
    }

    @Override
    public void onContextRefreshed() {
        if (validator.isEmpty(host))
            return;

        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(total);
        config.setMaxIdle(idle);
        config.setMaxWaitMillis(wait);
        config.setTestOnBorrow(true);
        if (validator.isEmpty(password))
            pool = new JedisPool(config, host, port);
        else
            pool = new JedisPool(config, host, port, 2000, password);
    }
}
