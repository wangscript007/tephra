package org.lpw.tephra.office.pptx.parser;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.lpw.tephra.office.pptx.MediaWriter;

/**
 * @author lpw
 */
public interface Parsers {
    /**
     * 解析数据。
     *
     * @param xslfShape   形状。
     * @param mediaWriter 媒体资源输出器。
     * @param shape       解析数据。
     */
    void parse(XSLFShape xslfShape, MediaWriter mediaWriter, JSONObject shape);
}
