package org.lpw.tephra.ctrl.socket;

import org.lpw.tephra.ctrl.context.ResponseSender;

/**
 * Socket支持。
 *
 * @author lpw
 */
public interface SocketHelper extends ResponseSender {
    /**
     * 绑定。
     *
     * @param sessionId       Socket session ID。
     * @param tephraSessionId Tephra session ID。
     */
    void bind(String sessionId, String tephraSessionId);

    /**
     * 解除绑定。
     *
     * @param sessionId       Socket session ID，为null则不使用。
     * @param tephraSessionId Tephra session ID，为null则不使用。
     */
    void unbind(String sessionId, String tephraSessionId);

    /**
     * 发送数据到客户端。
     *
     * @param sessionId Session ID，可以是Socket session ID或Tephra session ID，null则使用当前用户Session ID。
     * @param message   输出数据。
     */
    void send(String sessionId, byte[] message);
}
