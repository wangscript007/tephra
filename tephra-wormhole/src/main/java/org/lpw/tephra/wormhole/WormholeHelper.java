package org.lpw.tephra.wormhole;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

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
     * @param protocol 协议。
     * @param uri      URI地址。
     * @param internal 是否为内部网络。
     * @return URL地址。
     */
    String getUrl(Protocol protocol, String uri, boolean internal);

    /**
     * 获取WebSocket URL地址。
     *
     * @param ssl 是否SSL。
     * @return WebSocket URL地址。
     */
    String getWsUrl(boolean ssl);

    /**
     * 添加认证。
     *
     * @param type   类型。
     * @param token  认证Token。
     * @param ticket 认证Ticket。
     * @return 添加成功则返回true；否则返回false。
     */
    boolean auth(AuthType type, String token, String ticket);

    /**
     * 提交POST请求。
     *
     * @param uri            URI。
     * @param requestHeaders HTTP头信息集。
     * @param parameters     参数集。
     * @return 如果成功则返回页面数据；否则返回null。
     */
    String post(String uri, Map<String, String> requestHeaders, Map<String, String> parameters);

    /**
     * 提交POST请求。
     *
     * @param uri            URI。
     * @param requestHeaders HTTP头信息集。
     * @param parameters     参数集。
     * @return 如果成功则返回页面数据；否则返回null。
     */
    String post(String uri, Map<String, String> requestHeaders, String parameters);

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
     * 复制临时文件。
     *
     * @param uri 源文件URI。
     * @return 临时文件URI。
     */
    String temporary(String uri);

    /**
     * 下载文件。
     *
     * @param uri  文件URI地址。
     * @param file 文件保存绝对路径。
     */
    void download(String uri, String file);
}
