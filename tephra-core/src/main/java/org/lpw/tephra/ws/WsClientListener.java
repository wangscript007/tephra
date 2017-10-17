package org.lpw.tephra.ws;

/**
 * @author lpw
 */
public interface WsClientListener {
    /**
     * 连接完成。当连接完成时回调此方法。
     */
    void connect();

    /**
     * 接收数据。当监听端口接收到数据时，通知此方法。
     *
     * @param message 数据。
     */
    void receive(String message);

    /**
     * 连接被断开。当连接被断开时回调此方法。
     */
    void disconnect();
}
