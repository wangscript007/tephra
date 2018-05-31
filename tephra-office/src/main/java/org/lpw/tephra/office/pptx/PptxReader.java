package org.lpw.tephra.office.pptx;

import com.alibaba.fastjson.JSONObject;

import java.io.InputStream;

/**
 * PPTx读取器。
 *
 * @author lpw
 */
public interface PptxReader {
    /**
     * 读取并解析PPTX文件数据。
     * @param inputStream 输入流。
     * @param mediaWriter 媒体输出器。
     * @return JSON数据。
     */
    JSONObject read(InputStream inputStream, MediaWriter mediaWriter);
}
