package org.lpw.tephra.crypto;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.lang.reflect.Method;

/**
 * @author lpw
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:**/spring.xml"})
public class DigestTest {
    @Inject
    private Digest digest;

    @Test
    public void md5() {
        Assert.assertNull(digest.md5((String) null));
        Assert.assertNull(digest.md5((byte[]) null));
        Assert.assertEquals("c10f77963a2b21079156a0e5c5a4bb3c", digest.md5("digest"));
        Assert.assertEquals("c10f77963a2b21079156a0e5c5a4bb3c", digest.md5("digest".getBytes()));
    }

    @Test
    public void sha1() {
        Assert.assertNull(digest.sha1((String) null));
        Assert.assertNull(digest.sha1((byte[]) null));
        Assert.assertEquals("2923f6fa36614586ea09b4424b438915cc1b9b67", digest.sha1("digest"));
        Assert.assertEquals("2923f6fa36614586ea09b4424b438915cc1b9b67", digest.sha1("digest".getBytes()));
    }

    @Test
    public void digest() throws Exception {
        Method method = DigestImpl.class.getDeclaredMethod("digest", String.class, byte[].class);
        method.setAccessible(true);
        Assert.assertNull(method.invoke(digest, "algorithm", "digest".getBytes()));
    }
}
