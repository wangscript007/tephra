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
public class Des3Test {
    @Autowired
    protected Des3 des3;

    @Test
    public void crypto() {
        Assert.assertNull(des3.encrypt(null, null));
        byte[] key = "123456781234567812345678".getBytes();
        Assert.assertNull(des3.encrypt(key, null));
        byte[] text = "text".getBytes();
        Assert.assertNull(des3.encrypt(null, text));
        Assert.assertNull(des3.decrypt(key, text));
        Assert.assertArrayEquals(text, des3.decrypt(key, des3.encrypt(key, text)));
        key = "12345678".getBytes();
        Assert.assertNull(des3.encrypt(key, text));
    }
}
