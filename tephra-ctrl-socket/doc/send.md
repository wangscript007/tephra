# 推送数据到客户端

使用SocketHelper可主动推送数据到客户端，接口描述如下：
```java
package org.lpw.tephra.ctrl.socket;

/**
 * Socket支持。
 *
 * @author lpw
 */
public interface SocketHelper {
    /**
     * 发送数据到客户端。
     *
     * @param sessionId Session ID，可以是Socket session ID或Tephra session ID，null则使用当前用户Session ID。
     * @param message   数据。
     */
    void send(String sessionId, byte[] message);
}
```