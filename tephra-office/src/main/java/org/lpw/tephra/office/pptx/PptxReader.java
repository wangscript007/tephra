package org.lpw.tephra.office.pptx;

import com.alibaba.fastjson.JSONObject;

import java.io.InputStream;

/**
 * PPTx读取器。
 *
 * @author lpw
 */
public interface PptxReader {
    JSONObject read(InputStream inputStream, MediaWriter mediaWriter);
}
