package org.lpw.tephra.ctrl.upload;

import org.lpw.tephra.storage.Storage;

import java.io.IOException;
import java.io.InputStream;

/**
 * 上传读取器。
 *
 * @author lpw
 */
public interface UploadReader {
    /**
     * 获取域名称[监听器KEY]。
     *
     * @return 域名称[监听器KEY]。
     */
    String getFieldName();

    /**
     * 获取文件名。
     *
     * @return 文件名。
     */
    String getFileName();

    /**
     * 获取文件类型。
     *
     * @return 文件类型。
     */
    String getContentType();

    /**
     * 获取文件大小。
     *
     * @return 文件大小。
     */
    long getSize();

    /**
     * 获取输入流。
     *
     * @return 输入流。
     */
    InputStream getInputStream();

    /**
     * 获取数据。
     *
     * @return 数据。
     */
    byte[] getBytes();

    /**
     * 将上传文件写入存储器。
     *
     * @param storage 存储器。
     * @param path    存储路径。
     * @throws IOException IO异常。
     */
    void write(Storage storage, String path) throws IOException;

    /**
     * 删除缓存文件。
     *
     * @throws IOException IO异常。
     */
    void delete() throws IOException;
}
