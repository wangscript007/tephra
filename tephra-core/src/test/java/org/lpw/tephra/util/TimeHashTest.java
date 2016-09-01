package org.lpw.tephra.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author lpw
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:**/spring.xml"})
public class TimeHashTest {
    @Autowired
    protected Thread thread;
    @Autowired
    protected TimeHash timeHash;

    @Test
    public void valid() {
        ((TimeHashImpl) timeHash).range_$eq(10);
        ((TimeHashImpl) timeHash).onContextRefreshed();

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
