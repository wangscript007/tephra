# Ctrl-Socket简介
Ctrl-Socket模块提供基于Socket的请求处理，并将请求提交给Ctrl进行调度处理。

Ctrl-Socket模块接收JSON格式的数据，并且当发送数据大小超过设置阀值时，自动进行压缩。

## 数据包格式
- 数据包第0-4个字节表示本次发包的总长度，包括自身;
- 第5-8个字节表示数据压缩前的长度，如果未启用压缩则总是为0。
- 第9个字节开始为数据。
> 接收与发送均使用此数据格式。

## 接收数据格式
接收的数据为JSON格式，相关描述如下：
```json
{
    "tephra-session-id": "字符串，全局的Session ID；如果未提供则使用系统默认的Session ID。",
    "uri": "字符串，请求URI地址。",
    "header": "JSON对象，需要的头信息。",
    "request": "JSON对象，请求参数。"
}
```

[推送数据到客户端](doc/send.md)

[配置参数](src/main/resources/socket.ctrl.tephra.config)