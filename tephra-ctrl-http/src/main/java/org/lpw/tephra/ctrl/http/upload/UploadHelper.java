package org.lpw.tephra.ctrl.http.upload;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author lpw
 */
public interface UploadHelper {
    /**
     * 前缀。
     */
    String PREFIX = "tephra.ctrl.http.upload.";
    /**
     * 上传Servlet URI地址。
     */
    String UPLOAD = "/tephra/ctrl-http/upload";
    /**
     * 上传Servlet URI地址。
     */
    String UPLOAD_PATH = "/tephra/ctrl-http/upload-path";

    /**
     * 上传文件。
     *
     * @param request  请求HttpServletRequest信息。
     * @param response 输出HttpServletResponse信息。
     * @param uploader 上传器名称。
     */
    void upload(HttpServletRequest request, HttpServletResponse response, String uploader);
}
