# 转发请求

CtrlHttp提供了转发HTTP请求的功能，以`/redirect`开始的URI请求都将被转发。如：
```text
/redirect
/redirect/your/uri/path
```

转发请求需提供`key`参数，参数值配置在`/WEB-INF/http/redirect`中（此配置由`tephra.ctrl.http.redirect`指定）
```
## 配置转发路径。
## 使用【key=url】的方式，每行一个配置。
tephra = https://github.com/heisedebaise/tephra?name=value
```
则当请求`/redirect?key=tephra`时将被redirect到`https://github.com/heisedebaise/tephra?name=value`；而请求`/redirect/your/uri/path?key=tephra`时将被redirect到`https://github.com/heisedebaise/tephra/your/uri/path?name=value`。