package org.lpw.tephra.util;

import org.junit.Assert;
import org.junit.Test;
import org.lpw.tephra.test.CoreTestSupport;

import javax.inject.Inject;

/**
 * @author lpw
 */
public class TimeHashTest extends CoreTestSupport {
    @Inject
    private Thread thread;
    @Inject
    private TimeHash timeHash;

    @Test
    public void valid() {
        ((TimeHashImpl) timeHash).range_$eq(10);

        int[] ns = new int[5];
        for (int i = 0; i < ns.length; i++) {
            ns[i] = timeHash.generate();
            thread.sleep(0, 1, TimeUnit.Second);
        }
        for (int n : ns)
            Assert.assertTrue(timeHash.valid(n));
        for (int i = 0; i < 100000000; i = i * 2 + 1)
            Assert.assertFalse(timeHash.valid(i));
        Assert.assertTrue(timeHash.isEnable());

        thread.sleep(10, TimeUnit.Second);

        for (int n : ns)
            Assert.assertFalse(timeHash.valid(n));

        ((TimeHashImpl) timeHash).range_$eq(0);
        for (int n : ns)
            Assert.assertTrue(timeHash.valid(n));
        Assert.assertFalse(timeHash.isEnable());
    }
}
