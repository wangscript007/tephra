package org.lpw.tephra.ctrl.upload;

import com.alibaba.fastjson.JSONArray;

import java.io.IOException;
import java.util.List;

/**
 * @author lpw
 */
public interface UploadService {
    /**
     * 前缀。
     */
    String PREFIX = "tephra.ctrl.upload.";

    /**
     * 上传文件保存根路径。
     */
    String ROOT = "/upload/";

    /**
     * 处理上传请求。
     *
     * @param content 上传数据。
     * @return 处理结果。
     */
    JSONArray upload(String content);

    /**
     * 处理上传请求。
     *
     * @param readers 上传数据读取实例。
     * @return 处理结果。
     * @throws IOException IO异常。
     */
    JSONArray upload(List<UploadReader> readers) throws IOException;

    /**
     * 删除上传的文件。
     *
     * @param key 上传key。
     * @param uri 文件URI地址。
     */
    void remove(String key, String uri);
}
