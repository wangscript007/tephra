# 使用MongoDB持久化数据
Dao模块提供了对于MongoDB的支持，需要配置dao.tephra.config.properties：
```properties
## 设置数据库IP地址+端口号集。
## 使用JSON数据格式，每个设置对象包含key与values属性，其中
## * key 为数据库引用名称，空则为默认数据库；必须有且仅有一个key设置为空。
## * username 数据库登入用户名。
## * password 数据库登入密码。
## * ips 指定数据库的访问IP地址+":"+端口号，如果设置多个值则自动进行负载均衡。
## * schema 数据库名。
#tephra.dao.mongo.config = [{key:"",username:"root",password:"root",ips:["127.0.0.1:27017"],schema:"d_tephra"}]
```
Mongo接口说明：
```java
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
```
简单的增删改查：
```java
package org.lpw.tephra.dao.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import net.sf.json.JSONObject;
import org.bson.Document;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lpw.tephra.util.Generator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author lpw
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:**/spring.xml"})
public class MongoTest {
    @Autowired
    protected Generator generator;
    @Autowired
    protected Mongo mongo;

    @Test
    public void crud() {
        mongo.create(JSONObject.fromObject("{key:\"\",username:\"root\",password:\"root\",ips:[\"127.0.0.1:27017\"],schema:\"d_tephra_test\"}"));
        MongoCollection<Document> collection = mongo.getCollection(null, "test");
        JSONObject object = new JSONObject();
        object.put("id", generator.uuid());
        object.put("name", "hello");
        collection.insertOne(Document.parse(object.toString()));
        int count = 0;
        for (Document document : collection.find()) {
            if (object.getString("id").equals(document.getString("id"))) {
                count++;
                Assert.assertEquals("hello", document.getString("name"));
            }
        }
        Assert.assertEquals(1, count);

        collection.updateOne(Filters.eq("id", object.getString("id")), new Document("$set", Document.parse("{name:'new name'}")));
        count = 0;
        for (Document document : collection.find()) {
            if (object.getString("id").equals(document.getString("id"))) {
                count++;
                Assert.assertEquals("new name", document.getString("name"));
            }
        }
        Assert.assertEquals(1, count);

        collection.deleteOne(Filters.eq("id", object.getString("id")));
        count = 0;
        for (Document document : collection.find()) {
            if (object.getString("id").equals(document.getString("id")))
                count++;
        }
        Assert.assertEquals(0, count);
    }
}
```