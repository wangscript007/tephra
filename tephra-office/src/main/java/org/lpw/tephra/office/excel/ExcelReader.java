package org.lpw.tephra.office.excel;

import com.alibaba.fastjson.JSONObject;

import java.io.InputStream;

/**
 * Excel读取器。
 *
 * @author lpw
 */
public interface ExcelReader {
    JSONObject read(InputStream inputStream);
}
