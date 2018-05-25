package org.lpw.tephra.ctrl.context;

import java.io.ByteArrayOutputStream;

/**
 * 输出发送器。
 *
 * @author lpw
 */
public interface ResponseSender {
    /**
     * 发送数据到客户端。
     *
     * @param sessionId             Session ID，可以是Socket session ID或Tephra session ID，null则使用当前用户Session ID。
     * @param byteArrayOutputStream 输出数据流。
     */
    void send(String sessionId, ByteArrayOutputStream byteArrayOutputStream);
}
