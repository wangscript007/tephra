package org.lpw.tephra.pdf;

import com.alibaba.fastjson.JSONObject;

import java.io.InputStream;

/**
 * PDF读取器。
 *
 * @author lpw
 */
public interface PdfReader {
    /**
     * 读取并解析PDF数据。
     *
     * @param inputStream 输入流。
     * @param mediaWriter 媒体输出器。
     * @return JSON数据。
     */
    JSONObject read(InputStream inputStream, MediaWriter mediaWriter);
}
