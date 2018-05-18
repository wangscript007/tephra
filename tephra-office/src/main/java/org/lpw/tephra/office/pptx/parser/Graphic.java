package org.lpw.tephra.office.pptx.parser;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xslf.usermodel.XSLFGraphicFrame;
import org.lpw.tephra.office.pptx.MediaWriter;

/**
 * 图表形状解析器。
 *
 * @author lpw
 */
public interface Graphic {
    /**
     * 获取处理顺序。
     *
     * @return 处理顺序。
     */
    int getSort();

    /**
     * 解析数据。
     *
     * @param xslfGraphicFrame 形状。
     * @param mediaWriter      媒体资源输出器。
     * @param shape            解析数据。
     */
    void parse(XSLFGraphicFrame xslfGraphicFrame, MediaWriter mediaWriter, JSONObject shape);
}
