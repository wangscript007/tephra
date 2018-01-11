package org.lpw.tephra.cache.redis;

import org.lpw.tephra.bean.ContextRefreshedListener;
import org.lpw.tephra.cache.Handler;
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
        Jedis jedis = pool.getResource();
        jedis.set(key.getBytes(), serializer.serialize(value));
        jedis.close();
    }

    @Override
    public <T> T get(String key) {
        return get(key, false);
    }

    @Override
    public <T> T remove(String key) {
        return get(key, true);
    }

    @SuppressWarnings({"unchecked"})
    private <T> T get(String key, boolean remove) {
        Jedis jedis = pool.getResource();
        byte[] k = key.getBytes();
        byte[] bytes = jedis.get(k);
        if (remove)
            jedis.del(k);
        jedis.close();

        return (T) serializer.unserialize(bytes);
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
