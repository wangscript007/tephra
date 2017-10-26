package org.lpw.tephra.aio;

/**
 * 客户端管理器。
 *
 * @author lpw
 */
public interface AioClients {
    /**
     * 获取一个新的AIO客户端连接实例。
     *
     * @return 新的AIO客户端连接实例。
     */
    AioClient get();

    /**
     * 关闭所有AIO客户端连接。
     */
    void close();
}
