package org.lpw.tephra.wormhole;

import java.io.File;
import java.io.InputStream;

/**
 * @author lpw
 */
public interface WormholeHelper {
    /**
     * 验证是否为图片URI地址。
     *
     * @param uri URI地址。
     * @return 如果是则返回true；否则返回false。
     */
    boolean isImageUri(String uri);

    /**
     * 验证是否为文件URI地址。
     *
     * @param uri URI地址。
     * @return 如果是则返回true；否则返回false。
     */
    boolean isFileUri(String uri);

    /**
     * 获取URL地址。
     *
     * @param uri URI地址。
     * @return URL地址。
     */
    String getUrl(String uri);

    /**
     * 获取WebSocket URL地址。
     *
     * @return WebSocket URL地址。
     */
    String getWebSocketUrl();

    /**
     * 保存图片。
     *
     * @param path        目录。
     * @param name        名称。
     * @param suffix      文件名后缀。
     * @param sign        签名密钥名。
     * @param inputStream 输入流。
     * @return URI地址。
     */
    String image(String path, String name, String suffix, String sign, InputStream inputStream);

    /**
     * 保存图片。
     *
     * @param path 目录。
     * @param name 名称。
     * @param sign 签名密钥名。
     * @param file 文件。
     * @return URI地址。
     */
    String image(String path, String name, String sign, File file);

    /**
     * 保存文件。
     *
     * @param path        目录。
     * @param name        名称。
     * @param suffix      文件名后缀。
     * @param sign        签名密钥名。
     * @param inputStream 输入流。
     * @return URI地址。
     */
    String file(String path, String name, String suffix, String sign, InputStream inputStream);

    /**
     * 保存文件。
     *
     * @param path 目录。
     * @param name 名称。
     * @param sign 签名密钥名。
     * @param file 文件。
     * @return URI地址。
     */
    String file(String path, String name, String sign, File file);

    /**
     * 下载文件。
     *
     * @param uri  文件URI地址。
     * @param file 文件保存绝对路径。
     */
    void download(String uri, String file);
}
