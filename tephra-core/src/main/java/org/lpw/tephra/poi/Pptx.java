package org.lpw.tephra.poi;

import com.alibaba.fastjson.JSONObject;

import java.io.OutputStream;

/**
 * PPTx处理器。
 * @author lpw
 */
public interface Pptx {
    void write(JSONObject object, OutputStream outputStream);
}
