# 使用WebSocket

CtrlHttp模块提供了一个标准的WebSocket监听服务，可以在客户端建立与`/tephra/ctrl-http/ws`的通信，如：

```html
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>WebSocket</title>
</head>
<body>
</body>
<script type="text/javascript">
    var ws;

    if (window.WebSocket) {
        ws = new WebSocket("ws://localhost:8080/tephra/ctrl-http/ws");
        ws.onopen = function (event) {
            console.log("open");

            ws.send(JSON.stringify({
                uri: "/tephra/ctrl/status"
            }));
        };
 
        ws.onmessage = function (event) {
            console.log("receive:" + event.data);
        };
 
        ws.onclose = function (event) {
            console.log("close");
        };
    }
    else
        alert("WebSocket not support !");
</script>
</html>
```

客户端请求时发送如下格式的JSON数据，可以访问服务端提供的服务：

```json
{
    "id": "请求ID值，用于标记一个请求，推送请求结果时，将包含此ID值。",
    "tephra-session-id": "字符串，全局的Session ID；如果未提供则使用系统默认的Session ID。",
    "uri": "字符串，请求URI地址。",
    "header": "JSON对象，需要的头信息。",
    "request": "JSON对象，请求参数。"
}
```

服务端可以借助`WsHelper.send`向客户端主动推送数据或关闭连接：

```java
package org.lpw.tephra.ctrl.http.ws;

import org.lpw.tephra.ctrl.context.ResponseSender;

import javax.websocket.Session;

/**
 * WebSocket支持类。
 *
 * @author lpw
 */
public interface WsHelper extends ResponseSender {
    /**
     * 发送消息到客户端。
     *
     * @param sessionId 客户端Session ID。
     * @param message 消息。
     */
    void send(String sessionId, String message);

    /**
     * 发送消息到所有客户端。
     *
     * @param message 消息。
     */
    void send(String message);

    /**
     * 关闭客户端连接。
     *
     * @param sessionId 客户端Session ID。
     */
    void close(String sessionId);
}
```
