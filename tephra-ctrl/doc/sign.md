# 请求参数签名
通过对请求参数进行签名与验证，可以有效防止参数在传输过程中被修改，并且能非法的API请求。
1、设置密钥 & 有效时长：
```property
## 请求签名密钥。
#tephra.ctrl.context.request.sign.key =
## 请求签名有效时长，单位：毫秒。
#tephra.ctrl.context.request.sign.time = 10000
```
2、在请求端，可通过request添加签名：
```java
package org.lpw.tephra.ctrl.context;

import java.util.Map;

/**
 * 请求。
 *
 * @author lpw
 */
public interface Request {
    /**
     * 添加请求消息摘要验证串。
     *
     * @param map 参数集。
     */
    void putSign(Map<String, String> map);
}
```
3、在服务端，可通过Validators.SIGN验证器进行验证：
```java
    @Execute(name = "query", validates = {
            @Validate(validator = Validators.SIGN, failureCode = 91)
    })
```
