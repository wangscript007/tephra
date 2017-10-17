package org.lpw.tephra.ws;

/**
 * WebSocket客户端。
 *
 * @author lpw
 */
public interface WsClient {
    void connect(WsClientListener listener, String url);

    void send(String message);

    void close();
}
