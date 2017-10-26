package org.lpw.tephra.aio;

/**
 * AIO服务器集。
 *
 * @author lpw
 */
public interface AioServers {
    /**
     * 获取一个新的AIO服务器实例。
     *
     * @return 新的AIO服务器实例。
     */
    AioServer get();

    /**
     * 关闭所有AIO服务器。
     */
    void close();
}
