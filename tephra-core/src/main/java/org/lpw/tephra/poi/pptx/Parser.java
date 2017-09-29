package org.lpw.tephra.poi.pptx;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;

/**
 * PPTx解析器。
 *
 * @author lpw
 */
public interface Parser {
    /**
     * 获取解析器类型。
     *
     * @return 解析器类型。
     */
    String getType();

    /**
     * 解析。
     *
     * @param xmlSlideShow PPTx实例。
     * @param xslfSlide    Slide实例。
     * @param object       数据。
     */
    void parse(XMLSlideShow xmlSlideShow, XSLFSlide xslfSlide, JSONObject object);
}
