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
