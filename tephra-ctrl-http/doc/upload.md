# 上传文件

CtrlHttp提供了文件上传实现。

1、配置http.ctrl.tephra.config.properties设置允许上传的最大文件大小。
```properties
## 设置文件上传最大文件大小。
#tephra.ctrl.http.upload.max-size = 1m
```

2、实现UploadListener或配置JSON，具体参考[ctrl上传](../../tephra/ctrl/doc/upload.md)说明。

3、客户端上传文件。
```html
<form action="/tephra/ctrl-http/upload" enctype="multipart/form-data">
    <input type="file" name="${key}">
    <button type="submit">提交</button>
</form>
```
> ${key}为上传处理器UploadListener.getKey的值。
