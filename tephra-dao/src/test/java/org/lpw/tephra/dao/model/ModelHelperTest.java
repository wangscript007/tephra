package org.lpw.tephra.dao.model;

import com.alibaba.fastjson.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.lpw.tephra.test.DaoTestSupport;
import org.lpw.tephra.util.DateTime;

import javax.inject.Inject;
import java.sql.Timestamp;

/**
 * @author lpw
 */
public class ModelHelperTest extends DaoTestSupport {
    @Inject
    private DateTime dateTime;
    @Inject
    private ModelHelper modelHelper;

    @Test
    public void toJson() {
        TestModel model = new TestModel();
        model.setId("id");
        model.setString("string");
        model.setInteger1(1);
        model.setInteger2(2L);
        model.setDecimal1(3.3F);
        model.setDecimal2(4.4D);
        Timestamp now = dateTime.now();
        model.setDate(dateTime.today());
        model.setTimestamp(now);
        model.setExtend1("{\"id\":\"extend id\",\"name\":\"extend 1\"}");
        model.setExtend2("{\"string\":\"extend string\",\"number\":5}");
        model.setExtend3("{\"value\":\"extend 3\"");
        model.setExtend4("{\"key\":\"extend 4\"}");
        model.setJson("json");
        JSONObject object = modelHelper.toJson(model);
        Assert.assertEquals(12, object.size());
        Assert.assertEquals("id", object.getString("id"));
        Assert.assertEquals("string", object.getString("string"));
        Assert.assertEquals(1, object.getIntValue("integer1"));
        Assert.assertEquals(2L, object.getLongValue("integer2"));
        Assert.assertEquals(3.3F, object.getFloatValue("decimal1"), 0.001F);
        Assert.assertEquals(4.4D, object.getDoubleValue("decimal2"), 0.001D);
        Assert.assertEquals(dateTime.toString(now, "yyyy-MM-dd"), object.getString("date"));
        Assert.assertEquals(dateTime.toString(now, "yyyy-MM-dd HH:mm:ss"), object.getString("timestamp"));
        Assert.assertEquals("extend 1", object.getString("name"));
        Assert.assertEquals(5, object.getIntValue("number"));
        Assert.assertEquals("{\"key\":\"extend 4\"}", object.getString("extend4"));
        JSONObject extend = object.getJSONObject("extend");
        Assert.assertEquals(2, extend.size());
        Assert.assertEquals("extend id", extend.getString("id"));
        Assert.assertEquals("extend string", extend.getString("string"));
    }
}
