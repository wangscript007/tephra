package org.lpw.tephra.ctrl;

import net.sf.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.lpw.tephra.ctrl.mock.MockHelper;
import org.lpw.tephra.test.DaoTestSupport;

import javax.inject.Inject;

/**
 * @author lpw
 */
public class FailureCodeTest extends DaoTestSupport {
    @Inject
    private MockHelper mockHelper;
    @Inject
    private FailureCode failureCode;

    @Test
    public void get() {
        mockHelper.reset();
        mockHelper.mock("/tephra/ctrl/failure-code/execute");
        JSONObject json = mockHelper.getResponse().asJson();
        Assert.assertEquals(100101, json.getInt("code"));
        for (int i = 0; i < 10; i++)
            Assert.assertEquals(100100 + i, failureCode.get(i));
    }
}
