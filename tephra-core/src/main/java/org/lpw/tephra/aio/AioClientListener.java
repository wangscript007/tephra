package org.lpw.tephra.aio;

/**
 * 客户端监听器。
 *
 * @author lpw
 */
public interface AioClientListener extends AioListener {
    /**
     * 连接完成。当连接完成时回调此方法。
     *
     * @param sessionId Session ID值。
     */
    void connect(String sessionId);
}
