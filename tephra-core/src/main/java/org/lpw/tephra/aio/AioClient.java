package org.lpw.tephra.aio;

/**
 * AIO客户端集。
 *
 * @author lpw
 */
public interface AioClient {
    /**
     * 连接远程服务器。
     *
     * @param host     服务器地址。
     * @param port     端口号。
     * @param listener 监听器。
     */
    void connect(String host, int port, AioClientListener listener);

    /**
     * 关闭连接。
     */
    void close();
}
