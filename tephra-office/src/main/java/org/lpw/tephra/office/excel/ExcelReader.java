package org.lpw.tephra.office.excel;

import com.alibaba.fastjson.JSONObject;
import org.lpw.tephra.office.MediaWriter;

import java.io.InputStream;

/**
 * Excel读取器。
 *
 * @author lpw
 */
public interface ExcelReader {
    /**
     * 读取并解析Excel数据。
     *
     * @param inputStream 输入流。
     * @param mediaWriter 媒体输出器。
     * @return JSON数据。
     */
    JSONObject read(InputStream inputStream, MediaWriter mediaWriter);
}
