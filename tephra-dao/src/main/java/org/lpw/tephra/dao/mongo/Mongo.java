package org.lpw.tephra.dao.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.sf.json.JSONObject;
import org.bson.Document;
import org.lpw.tephra.dao.Commitable;

/**
 * MongoDB操作接口。
 *
 * @author lpw
 */
public interface Mongo extends Commitable {
    /**
     * 获取Mongo数据库。
     *
     * @param key 配置key。
     * @return Mongo数据库；如果不存在则返回null。
     */
    MongoDatabase getDatabase(String key);

    /**
     * 获取Mongo文档集。
     *
     * @param key  配置key。
     * @param name 文档名。
     * @return Mongo文档集；如果不存在则返回null。
     */
    MongoCollection<Document> getCollection(String key, String name);

    /**
     * 创建连接。
     *
     * @param config 配置。
     */
    void create(JSONObject config);
}
