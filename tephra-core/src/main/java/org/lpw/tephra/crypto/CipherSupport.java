package org.lpw.tephra.crypto;

import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Validator;
import org.springframework.beans.factory.annotation.Autowired;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @auth lpw
 */
public abstract class CipherSupport {
    @Autowired
    protected Validator validator;
    @Autowired
    protected Logger logger;
    protected Map<StringBuilder, SecretKey> secretKeys = new ConcurrentHashMap<>();

    public byte[] encrypt(byte[] key, byte[] message) {
        return doFinal(key, message, Cipher.ENCRYPT_MODE);
    }

    public byte[] decrypt(byte[] key, byte[] message) {
        return doFinal(key, message, Cipher.DECRYPT_MODE);
    }

    protected byte[] doFinal(byte[] key, byte[] input, int mode) {
        if (validator.isEmpty(key) || validator.isEmpty(input))
            return null;

        try {
            Cipher cipher = Cipher.getInstance(getAlgorithm());
            cipher.init(mode, getSecretKey(key));

            return cipher.doFinal(input);
        } catch (Exception e) {
            logger.warn(e, "使用密钥[{}]进行[{}]加/解密[{}]时发生异常！", new String(key), getAlgorithm(), new String(input));

            return null;
        }
    }

    protected SecretKey getSecretKey(byte[] key) {
        if (!validate(key))
            return null;

        StringBuilder sb = new StringBuilder();
        sb.append(getAlgorithm());
        for (byte by : key)
            sb.append(by);

        SecretKey secretKey = secretKeys.get(sb);
        if (secretKey == null) {
            secretKey = new SecretKeySpec(key, getAlgorithm());
            secretKeys.put(sb, secretKey);
        }

        return secretKey;
    }

    /**
     * 获取算法。
     *
     * @return 算法。
     */
    protected abstract String getAlgorithm();

    /**
     * 验证密钥是否合法。
     *
     * @param key 密钥。
     * @return 如果合法则返回true；否则返回false。
     */
    protected abstract boolean validate(byte[] key);
}
