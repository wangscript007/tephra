package org.lpw.tephra.office;

import com.alibaba.fastjson.JSONObject;

import java.io.InputStream;

/**
 * 媒体资源输出器。
 *
 * @author lpw
 */
public interface MediaReader {
    /**
     * 读取媒体资源。
     *
     * @param object 数据。
     * @return 媒体资源输入流。
     */
    InputStream read(JSONObject object);
}
