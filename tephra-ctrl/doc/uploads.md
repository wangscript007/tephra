# 上传多文件

请求
- Service Key - tephra.ctrl.uploads
- URI - /tephra/ctrl/uploads

参数
```json
[
    {
        "name": "名称，须与监听器KEY相同",
        "fileName": "文件名",
        "contentType": "文件Content-Type",
        "base64": "BASE64编码后的数据",
        "string": "字符串数据",
        "url": "文件URL地址"
    }
]
```

> `base64/string/url`三选一，优先使用`banse64`，然后是`string`，最后是`url`。

> 其它参数可直接添加到请求参数列表。

返回
```
[
    {
        "success": "true/false",
        "name": "名称，须与监听器KEY相同",
        "fileName": "文件名",
        "path": "上传文件保存路径，成功时返回",
        "thumbnail": "缩略图文件保存路径，成功且上传文件为符合缩略图设置的图片时返回",
        "message": "错误信息，失败时返回"
    }
]
```

> 自定义配置参考[文件上传](upload.md)说明。
