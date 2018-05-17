package org.lpw.tephra.office.pptx.parser;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xslf.usermodel.XSLFSimpleShape;
import org.lpw.tephra.office.pptx.MediaWriter;

/**
 * @author lpw
 */
public interface Parsers {
    /**
     * 解析数据。
     *
     * @param xslfSimpleShape 形状。
     * @param mediaWriter     媒体资源输出器。
     * @param shape           解析数据。
     * @param layout          是否为模板形状。
     */
    void parse(XSLFSimpleShape xslfSimpleShape, MediaWriter mediaWriter, JSONObject shape, boolean layout);
}
