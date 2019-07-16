package org.lpw.tephra.office.excel;

import com.alibaba.fastjson.JSONObject;

import java.io.OutputStream;

/**
 * @author lpw
 */
public interface ExcelWriter {
    /**
     * 输出Excel。
     *
     * @param object       数据。
     * @param outputStream 输出流。
     * @return 成功则返回true；否则返回false。
     */
    boolean write(JSONObject object, OutputStream outputStream);
}
