package org.lpw.tephra.office.pptx.parser;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.lpw.tephra.office.MediaReader;

/**
 * @author lpw
 */
public interface Creater {
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
}
