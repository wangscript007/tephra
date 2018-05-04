package org.lpw.tephra.util;

import com.alibaba.fastjson.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.lpw.tephra.test.CoreTestSupport;

import javax.inject.Inject;

/**
 * @author lpw
 */
public class JsonTest extends CoreTestSupport {
    @Inject
    private Json json;

    @Test
    public void findObject() {
        JSONObject object = json.toObject("{\"a\":{\"b\":{\"c\":{\"d\":1},\"cc\":{\"d\":2}}}}");
        Assert.assertNull(json.findObject(object, "a", "c"));

        JSONObject obj = json.findObject(object, "a", "b", "c");
        Assert.assertEquals(1, obj.size());
        Assert.assertEquals(1, obj.getIntValue("d"));

        obj = json.findObject(object, "a", "b", "cc");
        Assert.assertEquals(1, obj.size());
        Assert.assertEquals(2, obj.getIntValue("d"));
    }
}
