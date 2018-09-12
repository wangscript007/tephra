package org.lpw.tephra.office.pptx;

import com.alibaba.fastjson.JSONObject;
import org.lpw.tephra.office.GeometryConverter;
import org.lpw.tephra.office.MediaWriter;

import java.io.InputStream;

/**
 * PPTx读取器。
 *
 * @author lpw
 */
public interface PptxReader {
    /**
     * 读取并解析PPTX数据。
     *
     * @param inputStream       输入流。
     * @param mediaWriter       媒体输出器。
     * @param geometryConverter 几何图形转换器。
     * @return JSON数据。
     */
    JSONObject read(InputStream inputStream, MediaWriter mediaWriter, GeometryConverter geometryConverter);
}
