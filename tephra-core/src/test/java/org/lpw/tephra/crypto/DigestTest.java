package org.lpw.tephra.crypto;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @auth lpw
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:**/spring.xml"})
public class DigestTest {
    @Autowired
    protected Digest digest;

    @Test
    public void md5() {
        String string = null;
        Assert.assertNull(digest.md5(string));
        byte[] bytes = null;
        Assert.assertNull(digest.md5(bytes));
        Assert.assertEquals("c10f77963a2b21079156a0e5c5a4bb3c", digest.md5("digest"));
        Assert.assertEquals("c10f77963a2b21079156a0e5c5a4bb3c", digest.md5("digest".getBytes()));
    }

    @Test
    public void sha1() {
        String string = null;
        Assert.assertNull(digest.sha1(string));
        byte[] bytes = null;
        Assert.assertNull(digest.sha1(bytes));
        Assert.assertEquals("2923f6fa36614586ea09b4424b438915cc1b9b67", digest.sha1("digest"));
        Assert.assertEquals("2923f6fa36614586ea09b4424b438915cc1b9b67", digest.sha1("digest".getBytes()));
    }
}
