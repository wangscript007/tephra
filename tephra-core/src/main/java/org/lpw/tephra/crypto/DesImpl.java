package org.lpw.tephra.crypto;

import org.springframework.stereotype.Component;

/**
 * @auth lpw
 */
@Component("tephra.crypto.des")
public class DesImpl extends CipherSupport implements Des {
    @Override
    protected String getAlgorithm() {
        return "DES";
    }

    @Override
    protected boolean validate(byte[] key) {
        if (key.length % 8 == 0)
            return true;

        logger.warn(null, "DES密钥[{}]长度[{}]必须是8的整数倍！", new String(key), key.length);

        return false;
    }
}
