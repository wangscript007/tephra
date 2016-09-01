package org.lpw.tephra.dao.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.bson.Document;
import org.lpw.tephra.bean.ContextRefreshedListener;
import org.lpw.tephra.util.Generator;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lpw
 */
@Repository("tephra.dao.mongo")
public class MongoImpl implements Mongo, ContextRefreshedListener {
    @Autowired
    protected Validator validator;
    @Autowired
    protected Generator generator;
    @Autowired
    protected Logger logger;
    @Value("${tephra.dao.database.max-active:5}")
    protected int maxActive;
    @Value("${tephra.dao.database.max-wait:5000}")
    protected int maxWait;
    @Value("${tephra.dao.mongo.config:}")
    protected String config;
    protected Map<String, String> schemas;
    protected Map<String, List<MongoClient>> mongos;

    @Override
    public MongoDatabase getDatabase(String key) {
        if (key == null)
            key = "";

        String schema = schemas.get(key);
        if (validator.isEmpty(schema))
            throw new NullPointerException("MongoDB引用key[" + key + "]不存在！");

        List<MongoClient> list = mongos.get(key);

        return list.get(generator.random(0, list.size() - 1)).getDatabase(schema);
    }

    @Override
    public MongoCollection<Document> getCollection(String key, String name) {
        MongoDatabase database = getDatabase(key);

        return database == null ? null : database.getCollection(name);
    }

    @Override
    public void create(JSONObject config) {
        String schema = config.getString("schema");
        if (validator.isEmpty(schema))
            throw new NullPointerException("未设置schema值[" + config + "]！");

        JSONArray array = config.getJSONArray("ips");
        if (array == null || array.size() == 0)
            throw new NullPointerException("未设置ips值[" + config + "]！");

        String username = config.getString("username");
        String password = config.getString("password");
        MongoClientOptions.Builder builder = MongoClientOptions.builder().connectionsPerHost(maxActive).maxWaitTime(maxWait);
        List<MongoClient> list = new ArrayList<>();
        for (int i = 0; i < array.size(); i++)
            list.add(new MongoClient(new MongoClientURI("mongodb://" + username + ":" + password + "@" + array.getString(i) + "/" + schema, builder)));
        String key = config.getString("key");
        schemas.put(key, schema);
        mongos.put(key, list);

        if (logger.isDebugEnable())
            logger.debug("Mongo数据库[{}]初始化完成。", config);
    }

    @Override
    public void rollback() {
    }

    @Override
    public void close() {
    }

    @Override
    public int getContextRefreshedSort() {
        return 5;
    }

    @Override
    public void onContextRefreshed() {
        if (validator.isEmpty(config) || schemas != null || mongos != null)
            return;

        schemas = new HashMap<>();
        mongos = new HashMap<>();
        JSONArray array = JSONArray.fromObject(config);
        if (array != null && array.size() > 0)
            for (int i = 0; i < array.size(); i++)
                create(array.getJSONObject(i));
    }
}
