package org.lpw.tephra.aio;

/**
 * AIO Socket服务器。
 *
 * @author lpw
 */
public interface AioServer {
    /**
     * 启动监听服务。
     *
     * @param thread   处理线程数。
     * @param port     监听端口号。
     * @param listener 监听器。
     */
    void listen(int thread, int port, AioServerListener listener);

    /**
     * 关闭监听服务。
     */
    void close();
}
