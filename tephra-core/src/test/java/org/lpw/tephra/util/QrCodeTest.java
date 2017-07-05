package org.lpw.tephra.util;

import org.junit.Assert;
import org.junit.Test;
import org.lpw.tephra.test.CoreTestSupport;

import javax.inject.Inject;

/**
 * @author lpw
 */
public class QrCodeTest extends CoreTestSupport {
    @Inject
    private QrCode qrCode;

    @Test
    public void cr() {
        String url = "https://github.com/heisedebaise/tephra";
        qrCode.create(url, 128, null, "target/qr.png");
        qrCode.create(url, 128, "src/test/logo.png", "target/qr-logo.png");
        Assert.assertEquals(url, qrCode.read("target/qr.png"));
        Assert.assertEquals(url, qrCode.read("target/qr-logo.png"));
    }
}
