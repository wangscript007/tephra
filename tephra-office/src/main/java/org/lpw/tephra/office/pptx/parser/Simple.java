package org.lpw.tephra.office.pptx.parser;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xslf.usermodel.XSLFSimpleShape;
import org.lpw.tephra.office.MediaWriter;

/**
 * 简单形状解析器。
 *
 * @author lpw
 */
public interface Simple {
    /**
     * 获取处理顺序。
     *
     * @return 处理顺序。
     */
    int getSort();

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
