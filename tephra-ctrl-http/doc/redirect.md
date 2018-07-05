# 转发请求

CtrlHttp提供了转发HTTP请求的功能，当请求`/tephra/ctrl-http/redirect`时，请求将被转发到`to`参数提供的URL地址。如请求：
```
http://host:port/tephra/ctrl-http/redirect?to=http%3A%2F%2Fnew-host%3Anew-port%2Furi%3Fname%3Dvalue%23anchor&args=values
```
将被转发到：
```
http://new-host:new-port/uri?name=value&args=values#anchor
```

转发URL的`host`需配置到`/WEB-INF/http/redirect.json`中（此配置由`tephra.ctrl.http.redirect`指定）。
```
{
    "uri": "/tephra/ctrl-http/redirect",
    "hosts": [
        ""
    ],
    "regexes": [
        ""
    ]
}
```
