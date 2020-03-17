package org.lpw.tephra.dao.jdbc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.tephra.bean.ContextRefreshedListener;
import org.lpw.tephra.dao.Mode;
import org.lpw.tephra.dao.dialect.Dialect;
import org.lpw.tephra.dao.dialect.DialectFactory;
import org.lpw.tephra.util.Generator;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author lpw
 */
@Repository("tephra.dao.jdbc.data-source")
public class DataSourceImpl implements org.lpw.tephra.dao.jdbc.DataSource, ContextRefreshedListener {
    @Inject
    private Validator validator;
    @Inject
    private Generator generator;
    @Inject
    private Logger logger;
    @Inject
    private Optional<PasswordDecryptor> decryptor;
    @Inject
    private DialectFactory dialectFactory;
    @Value("${tephra.dao.database.initial-size:0}")
    private int initialSize;
    @Value("${tephra.dao.database.max-active:5}")
    private int maxActive;
    @Value("${tephra.dao.database.max-wait:5000}")
    private int maxWait;
    @Value("${tephra.dao.database.test-interval:30000}")
    private int testInterval;
    @Value("${tephra.dao.database.remove-abandoned-timeout:60}")
    private int removeAbandonedTimeout;
    @Value("${tephra.dao.database.config:}")
    private String config;
    @Value("${tephra.dao.data-source.key:}")
    private String key;
    private Map<String, JSONObject> configs = new HashMap<>();
    private Map<String, Dialect> dialects = new HashMap<>();
    private Map<String, DataSource> writeables = new ConcurrentHashMap<>();
    private Map<String, List<DataSource>> readonlys = new ConcurrentHashMap<>();
    private Map<String, Boolean> readonly = new ConcurrentHashMap<>();
    private Map<String, AtomicInteger> failures = new ConcurrentHashMap<>();

    @Override
    public DataSource getWriteable(String key) {
        return writeables.get(getKey(key));
    }

    @Override
    public DataSource getReadonly(String key) {
        if (!hasReadonly(key))
            return getWriteable(key);

        List<DataSource> list = listReadonly(key);

        return list.get(generator.random(0, list.size() - 1));
    }

    @Override
    public synchronized void addGetFailure(String name, Mode mode, Throwable throwable) {
        AtomicInteger atomicInteger = failures.computeIfAbsent(name, key -> new AtomicInteger());
        logger.error(null, "获取数据库[{}:{}:{}:{}]连接失败[{}]！", name, mode,
                maxActive, atomicInteger.get(), throwable.getMessage());
        if (atomicInteger.incrementAndGet() < maxActive)
            return;

        logger.error(null, "获取数据库连接失败累计次数超过最大连接数，尝试重新创建连接池！");
        JSONObject config = configs.get(name);
        createDataSource(key, dialects.get(name), config.getString("username"), config.getString("password"),
                config.getJSONArray("ips"), config.getString("schema"));
        atomicInteger.set(0);
    }

    @Override
    public List<DataSource> listReadonly(String key) {
        return readonlys.get(getKey(key));
    }

    @Override
    public boolean hasReadonly(String key) {
        return readonly.getOrDefault(getKey(key), false);
    }

    @Override
    public Map<String, Dialect> getDialects() {
        return dialects;
    }

    @Override
    public Dialect getDialect(String key) {
        return dialects.get(getKey(key));
    }

    @Override
    public String getKey(String key) {
        return key == null ? this.key : key;
    }

    @Override
    public String getDefaultKey() {
        return key;
    }

    @Override
    public JSONObject getConfig(String key) {
        return configs.get(key);
    }

    @Override
    public int getContextRefreshedSort() {
        return 3;
    }

    @Override
    public void onContextRefreshed() {
        if (validator.isEmpty(config))
            return;

        JSONArray array = JSON.parseArray(config);
        for (int i = 0; i < array.size(); i++)
            create(array.getJSONObject(i));
    }

    @Override
    public synchronized void create(JSONObject config) {
        String key = config.getString("key");
        configs.put(key, config);
        Dialect dialect = dialectFactory.get(config.getString("type"));
        dialects.put(key, dialect);
        if (key != null && writeables.get(key) == null)
            createDataSource(key, dialect, config.getString("username"), config.getString("password"),
                    config.getJSONArray("ips"), config.getString("schema"));

        if (logger.isInfoEnable())
            logger.info("成功创建数据库[{}]连接池。", config);
    }

    private void createDataSource(String key, Dialect dialect, String username, String password, JSONArray ips, String schema) {
        for (int i = 0; i < ips.size(); i++) {
            org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource();
            dataSource.setDriverClassName(dialect.getDriver());
            dataSource.setUrl(dialect.getUrl(ips.getString(i), schema));
            dataSource.setUsername(username);
            if (decryptor.isPresent()) {
                password = decryptor.get().decrypt(password);
            }
            dataSource.setPassword(password);
            dataSource.setInitialSize(initialSize);
            dataSource.setMaxActive(maxActive);
            dataSource.setMaxIdle(maxActive);
            dataSource.setMinIdle(initialSize);
            dataSource.setMaxWait(maxWait);
            dataSource.setTestWhileIdle(false);
            dataSource.setTestOnBorrow(true);
            dataSource.setValidationQuery(dialect.getValidationQuery());
            dataSource.setTestOnReturn(false);
            dataSource.setValidationInterval(testInterval);
            dataSource.setTimeBetweenEvictionRunsMillis(testInterval);
            dataSource.setRemoveAbandonedTimeout(removeAbandonedTimeout);
            dataSource.setMinEvictableIdleTimeMillis(testInterval);
            dataSource.setRemoveAbandoned(true);
            dataSource.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;" +
                    "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");

            if (i == 0)
                writeables.put(key, dataSource);
            else {
                List<DataSource> list = readonlys.get(key);
                if (list == null)
                    list = Collections.synchronizedList(new ArrayList<>());
                list.add(dataSource);
                readonlys.put(key, list);
                readonly.put(key, true);
            }

            if (logger.isInfoEnable())
                logger.info("数据源[{}@{}]设置完成。", key, ips.getString(i));
        }
    }
}
