package org.lpw.tephra.util;

/**
 * 定义加密解密操作。
 *
 * @author lpw
 */
public interface Security {
    /**
     * 提取MD5消息摘要。
     *
     * @param text 源字符串。
     * @return MD5消息摘要；如果提取失败将返回null。
     */
    String md5(String text);

    /**
     * 提取MD5消息摘要。
     *
     * @param text 源数据。
     * @return MD5消息摘要；如果提取失败将返回null。
     */
    String md5(byte[] text);

    /**
     * 提取SHA1消息摘要。
     *
     * @param text 源字符串。
     * @return SHA1消息摘要；如果提取失败将返回null。
     */
    String sha1(String text);

    /**
     * 提取SHA1消息摘要。
     *
     * @param text 源数据。
     * @return SHA1消息摘要；如果提取失败将返回null。
     */
    String sha1(byte[] text);

    /**
     * 使用3DES算法进行加密。
     *
     * @param key   密钥。密钥长度必须为24个字节。
     * @param input 要加密的数据。
     * @return 加密后的数据。
     */
    byte[] encrypt3des(String key, String input);

    /**
     * 使用3DES算法进行加密。
     *
     * @param key   密钥。密钥长度必须为24个字节。
     * @param input 要加密的数据。
     * @return 加密后的数据。
     */
    byte[] encrypt3des(byte[] key, byte[] input);

    /**
     * 使用3DES算法进行解密。
     *
     * @param key   密钥。密钥长度必须为24个字节。
     * @param input 要解密的数据。
     * @return 解密后的数据。
     */
    byte[] decrypt3des(String key, byte[] input);

    /**
     * 使用3DES算法进行解密。
     *
     * @param key   密钥。密钥长度必须为24个字节。
     * @param input 要解密的数据。
     * @return 解密后的数据。
     */
    byte[] decrypt3des(byte[] key, byte[] input);
}
