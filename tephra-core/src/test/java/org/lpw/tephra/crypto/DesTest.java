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
public class DesTest {
    @Autowired
    protected Des des;

    @Test
    public void crypto() {
        Assert.assertNull(des.encrypt(null, null));
        byte[] key = "12345678".getBytes();
        Assert.assertNull(des.encrypt(key, null));
        byte[] text = "text".getBytes();
        Assert.assertNull(des.encrypt(null, text));
        Assert.assertNull(des.decrypt(key, text));
        Assert.assertArrayEquals(text, des.decrypt(key, des.encrypt(key, text)));
        key = "123456".getBytes();
        Assert.assertNull(des.encrypt(key, text));
    }
}
