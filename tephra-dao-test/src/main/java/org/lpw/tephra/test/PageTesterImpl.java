package org.lpw.tephra.test;

import com.alibaba.fastjson.JSONObject;
import org.junit.Assert;
import org.springframework.stereotype.Repository;

/**
 * @author lpw
 */
@Repository("tephra.test.tester.page")
public class PageTesterImpl implements PageTester {
    @Override
    public void assertCountSizeNumber(int count, int size, int number, JSONObject object) {
        Assert.assertEquals(count, object.getIntValue("count"));
        Assert.assertEquals(size, object.getIntValue("size"));
        Assert.assertEquals(number, object.getIntValue("number"));
    }
}
