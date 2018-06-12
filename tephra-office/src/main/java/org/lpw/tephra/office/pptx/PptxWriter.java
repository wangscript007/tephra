package org.lpw.tephra.office.pptx;

import com.alibaba.fastjson.JSONObject;
import org.lpw.tephra.office.MediaReader;

import java.io.OutputStream;

/**
 * PPTx写入器。
 *
 * @author lpw
 */
public interface PptxWriter {
    /**
     * 写入PPTx文件。
     *
     * @param outputStream 输出流。
     * @param object       数据。
     * @param mediaReader  媒体读取器。
     */
    void write(OutputStream outputStream, JSONObject object, MediaReader mediaReader);
}
