package org.lpw.tephra.ctrl;

import net.sf.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lpw.tephra.ctrl.mock.MockHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author lpw
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:**/spring.xml"})
public class FailureCodeTest {
    @Autowired
    protected MockHelper mockHelper;
    @Autowired
    protected FailureCode failureCode;

    @Test
    public void get() {
        JSONObject json = JSONObject.fromObject(mockHelper.mock("/tephra/ctrl/failure-code/execute").getOutputStream().toString());
        Assert.assertEquals(100101, json.getInt("code"));
        for (int i = 0; i < 10; i++)
            Assert.assertEquals(100100 + i, failureCode.get(i));
    }
}
