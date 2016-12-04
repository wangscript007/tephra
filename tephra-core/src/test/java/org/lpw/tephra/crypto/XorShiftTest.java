package org.lpw.tephra.crypto;

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
public class XorShiftTest {
    @Autowired
    protected XorShift xorShift;

    @Test
    public void crypto() {
        Assert.assertNull(xorShift.encrypt(null, null));
        Assert.assertNull(xorShift.decrypt(null, null));
        byte[] key = "12345678".getBytes();
        Assert.assertNull(xorShift.encrypt(key, null));
        Assert.assertNull(xorShift.decrypt(key, null));
        byte[] text = "text".getBytes();
        Assert.assertNull(xorShift.encrypt(null, text));
        Assert.assertNull(xorShift.decrypt(null, text));
        Assert.assertArrayEquals(text, xorShift.decrypt(key, xorShift.encrypt(key, text)));
        text = "texttexttexttexttexttext".getBytes();
        Assert.assertArrayEquals(text, xorShift.decrypt(key, xorShift.encrypt(key, text)));
    }
}
