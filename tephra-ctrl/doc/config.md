# ctrl.tephra.config.properties
```properties
## 设置服务最大并发处理数。
## 超过此设置值时系统将返回繁忙信息。
#tephra.ctrl.dispatcher.max = 512
## 设置没有权限或Session过期提示信息编码。
#tephra.ctrl.dispatcher.not-permit = 9997
## 设置系统繁忙时返回的提示信息编码。
#tephra.ctrl.dispatcher.busy = 9998
## 设置系统执行异常时返回的提示信息编码。
#tephra.ctrl.dispatcher.exception = 9999

## 设置获取真实IP的请求头名称。
## 如果为空则使用系统默认的方式获取；
## 如果配置了反响代理则设置为转发的头名称。
#tephra.ctrl.context.header.real-ip =
## 请求签名密钥。
#tephra.ctrl.context.request.sign.key =

## 控制台服务URI地址。
## 如果设置为空则不启用控制台。
#tephra.ctrl.console.uri = /tephra/ctrl/console
## 允许访问控制台的IP地址集，多个IP地址间以逗号分割。
## 如果设置为空则表示拒绝所有IP访问。
## 如果设置为*号则表示允许任意IP地址访问。
#tephra.ctrl.console.allow-ips =

## 状态服务URI地址。
## 访问状态服务可以查看当前服务器的状态信息。
## 如果设置为空则不启用状态服务。
#tephra.ctrl.status.uri = /tephra/ctrl/status

## 设置返回结果数据格式类型。
#tephra.ctrl.template.type = json

## 设置错误提示页面。
## 当用户没有访问权限或Session过期时，将返回该页面。
#tephra.ctrl.template.freemarker.not-permit = not-permit
## 当运行过程中发生异常时，将返回该页面。
#tephra.ctrl.template.freemarker.failure = failure

## 设置流错误提示图片地址。
## 当运行过程中发生异常时，将返回该文件。
#tephra.ctrl.template.stream.failure = failure.jpg
```