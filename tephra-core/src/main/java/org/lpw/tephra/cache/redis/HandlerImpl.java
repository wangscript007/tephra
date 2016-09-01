package org.lpw.tephra.cache.redis;

import org.lpw.tephra.bean.ContextRefreshedListener;
import org.lpw.tephra.cache.Handler;
import org.lpw.tephra.util.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author lpw
 */
@Component("tephra.cache.redis.handler")
public class HandlerImpl implements Handler, ContextRefreshedListener {
    @Autowired
    protected Converter converter;
    @Value("${tephra.cache.name:}")
    protected String name;
    @Value("${tephra.cache.redis.host:localhost}")
    protected String host;
    @Value("${tephra.cache.redis.max-total:500}")
    protected int total;
    @Value("${tephra.cache.redis.max-idle:5}")
    protected int idle;
    @Value("${tephra.cache.redis.max-wait:500}")
    protected long wait;
    protected JedisPool pool;

    @Override
    public String getName() {
        return "redis";
    }

    @Override
    public void put(String key, Object value, boolean resident) {
        Jedis jedis = pool.getResource();
        jedis.set(key, converter.toString(value));
        jedis.close();
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public <T> T get(String key) {
        Jedis jedis = pool.getResource();
        String value = jedis.get(key);
        jedis.close();

        return (T) value;
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public <T> T remove(String key) {
        Jedis jedis = pool.getResource();
        String value = jedis.get(key);
        jedis.del(key);
        jedis.close();

        return (T) value;
    }

    @Override
    public int getContextRefreshedSort() {
        return 6;
    }

    @Override
    public void onContextRefreshed() {
        if (!getName().equals(name))
            return;

        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(total);
        config.setMaxIdle(idle);
        config.setMaxWaitMillis(wait);
        config.setTestOnBorrow(true);
        pool = new JedisPool(config, host);
    }
}
