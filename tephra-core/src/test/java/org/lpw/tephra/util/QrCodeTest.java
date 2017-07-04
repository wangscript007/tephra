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
        qrCode.create("二维码内容", 128, null, "target/qr.png");
        qrCode.create("带LOGO的二维码", 128, "src/test/logo.png", "target/qr-logo.png");
        Assert.assertEquals("二维码内容", qrCode.read("target/qr.png"));
        Assert.assertEquals("带LOGO的二维码", qrCode.read("target/qr-logo.png"));
    }
}
