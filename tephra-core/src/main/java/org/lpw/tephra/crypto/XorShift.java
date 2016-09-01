package org.lpw.tephra.crypto;

/**
 * 异或移位加解密算法。
 *
 * @author lpw
 */
public interface XorShift {
    /**
     * 加密数据。
     *
     * @param key     密钥。
     * @param message 信息。
     * @return 密文。
     */
    byte[] encrypt(byte[] key, byte[] message);

    /**
     * 解密数据。
     *
     * @param key     密钥。
     * @param message 密文。
     * @return 信息。
     */
    byte[] decrypt(byte[] key, byte[] message);
}
