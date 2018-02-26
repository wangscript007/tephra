package org.lpw.tephra.crypto;

import org.junit.Assert;
import org.junit.Test;
import org.lpw.tephra.test.CoreTestSupport;
import org.lpw.tephra.util.Coder;

import javax.inject.Inject;

/**
 * @author lpw
 */
public class DigestTest extends CoreTestSupport {
    @Inject
    private Digest digest;
    @Inject
    private Coder coder;

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
    public void sha256() {
        Assert.assertNull(digest.sha256((String) null));
        Assert.assertNull(digest.sha256((byte[]) null));
        Assert.assertEquals("0bf474896363505e5ea5e5d6ace8ebfb13a760a409b1fb467d428fc716f9f284", digest.sha256("digest"));
        Assert.assertEquals("0bf474896363505e5ea5e5d6ace8ebfb13a760a409b1fb467d428fc716f9f284", digest.sha256("digest".getBytes()));
    }

    @Test
    public void sha512() {
        Assert.assertNull(digest.sha512((String) null));
        Assert.assertNull(digest.sha512((byte[]) null));
        Assert.assertEquals("cc136dcb98f13a4ac7dc7e1e97d0c832e63a96cab5685d7cd865d623fa4e6948f543d38799d4b0fc8bec740deab548e85742476765e0507e241bc1eeb0486821", digest.sha512("digest"));
        Assert.assertEquals("cc136dcb98f13a4ac7dc7e1e97d0c832e63a96cab5685d7cd865d623fa4e6948f543d38799d4b0fc8bec740deab548e85742476765e0507e241bc1eeb0486821", digest.sha512("digest".getBytes()));
    }

    @Test
    public void digest() throws Exception {
        Assert.assertNull(digest.digest("md5", null));
        Assert.assertNull(digest.digest("algorithm", "digest".getBytes()));
        Assert.assertEquals("c10f77963a2b21079156a0e5c5a4bb3c", coder.hex(digest.digest("MD5", "digest".getBytes())));
        Assert.assertEquals("2923f6fa36614586ea09b4424b438915cc1b9b67", coder.hex(digest.digest("SHA1", "digest".getBytes())));
        Assert.assertEquals("0bf474896363505e5ea5e5d6ace8ebfb13a760a409b1fb467d428fc716f9f284", coder.hex(digest.digest("SHA-256", "digest".getBytes())));
        Assert.assertEquals("cc136dcb98f13a4ac7dc7e1e97d0c832e63a96cab5685d7cd865d623fa4e6948f543d38799d4b0fc8bec740deab548e85742476765e0507e241bc1eeb0486821", coder.hex(digest.digest("SHA-512", "digest".getBytes())));
    }
}
