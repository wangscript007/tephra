package org.lpw.tephra.office;

import com.alibaba.fastjson.JSONObject;

import java.io.OutputStream;

/**
 * 媒体资源输出器。
 *
 * @author lpw
 */
public interface MediaReader {
    /**
     * 读取媒体资源。
     *
     * @param outputStream 输出流。
     * @param object       数据。
     * @return 媒体类型。
     */
    MediaType read(OutputStream outputStream, JSONObject object);
}
