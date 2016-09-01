package org.lpw.tephra.util;

import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lpw
 */
@Component("tephra.util.security")
public class SecurityImpl implements Security {
    private static final String MD5 = "MD5";
    private static final String SHA1 = "SHA1";
    private static final String DESEDE = "DESede";

    @Autowired
    protected Validator validator;
    @Autowired
    protected Logger logger;
    protected Map<StringBuilder, SecretKey> secretKeys = new ConcurrentHashMap<>();

    @Override
    public String md5(String text) {
        return text == null ? null : digest(MD5, text.getBytes());
    }

    @Override
    public String md5(byte[] text) {
        return text == null ? null : digest(MD5, text);
    }

    @Override
    public String sha1(String text) {
        return text == null ? null : digest(SHA1, text.getBytes());
    }

    @Override
    public String sha1(byte[] text) {
        return text == null ? null : digest(SHA1, text);
    }

    protected String digest(String algorithm, byte[] input) {
        if (input == null)
            return null;

        try {
            return Hex.encodeHexString(MessageDigest.getInstance(algorithm).digest(input));
        } catch (NoSuchAlgorithmException e) {
            logger.warn(e, "取消息摘要[{}]时发生异常！", algorithm);

            return null;
        }
    }

    @Override
    public byte[] encrypt3des(String key, String input) {
        if (validator.isEmpty(key) || validator.isEmpty(input))
            return null;

        return encrypt3des(key.getBytes(), input.getBytes());
    }

    @Override
    public byte[] encrypt3des(byte[] key, byte[] input) {
        if (validator.isEmpty(input))
            return null;

        return crypt3des(key, input, Cipher.ENCRYPT_MODE);
    }

    @Override
    public byte[] decrypt3des(String key, byte[] input) {
        if (validator.isEmpty(key))
            return null;

        return decrypt3des(key.getBytes(), input);
    }

    @Override
    public byte[] decrypt3des(byte[] key, byte[] input) {
        if (validator.isEmpty(input))
            return null;

        return crypt3des(key, input, Cipher.DECRYPT_MODE);
    }

    protected byte[] crypt3des(byte[] key, byte[] input, int mode) {
        if (validator.isEmpty(key) || validator.isEmpty(input))
            return null;

        try {
            Cipher cipher = Cipher.getInstance(DESEDE);
            cipher.init(mode, getSecretKey(key, DESEDE));

            return cipher.doFinal(input);
        } catch (Exception e) {
            logger.warn(e, "使用密钥[{}]进行3DES加/解密[{}]时发生异常！", new String(key), new String(input));

            return null;
        }
    }

    protected SecretKey getSecretKey(byte[] key, String algorithm) {
        if (DESEDE.equals(algorithm) && key.length != 24) {
            logger.warn(null, "3DES密钥[{}]长度[{}]必须是24个字节！", new String(key), key.length);

            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(algorithm);
        for (byte by : key)
            sb.append(by);

        SecretKey secretKey = secretKeys.get(sb);
        if (secretKey == null) {
            secretKey = new SecretKeySpec(key, algorithm);
            secretKeys.put(sb, secretKey);
        }

        return secretKey;
    }
}
