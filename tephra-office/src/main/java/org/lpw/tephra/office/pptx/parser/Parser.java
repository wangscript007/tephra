package org.lpw.tephra.office.pptx.parser;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFGraphicFrame;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSimpleShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.lpw.tephra.office.MediaReader;
import org.lpw.tephra.office.MediaWriter;

/**
 * 解析器。
 *
 * @author lpw
 */
public interface Parser {
    /**
     * 解析形状。
     *
     * @param xslfSimpleShape 形状。
     * @param mediaWriter     媒体资源输出器。
     * @param shape           解析数据。
     * @param layout          是否为模板形状。
     */
    void parseShape(XSLFSimpleShape xslfSimpleShape, MediaWriter mediaWriter, JSONObject shape, boolean layout);

    /**
     * 解析形状。
     *
     * @param xslfSlide        Slide。
     * @param xslfGraphicFrame 形状。
     * @param mediaWriter      媒体资源输出器。
     * @param shape            解析数据。
     */
    void parseShape(XSLFSlide xslfSlide, XSLFGraphicFrame xslfGraphicFrame, MediaWriter mediaWriter, JSONObject shape);

    /**
     * 创建形状。
     *
     * @param xmlSlideShow XMLSlideShow实例。
     * @param xslfSlide    XSLFSlide实例。
     * @param mediaReader  媒体读取器。
     * @param shape        形状数据。
     * @return 形状；如果创建失败则返回null。
     */
    XSLFShape createShape(XMLSlideShow xmlSlideShow, XSLFSlide xslfSlide, MediaReader mediaReader, JSONObject shape);

    /**
     * 解析数据。
     *
     * @param xslfSimpleShape 形状。
     * @param mediaReader     媒体资源读取器。
     * @param shape           解析数据。
     */
    void parseToShape(XSLFSimpleShape xslfSimpleShape, MediaReader mediaReader, JSONObject shape);
}
