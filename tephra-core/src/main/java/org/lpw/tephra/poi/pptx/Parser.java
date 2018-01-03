package org.lpw.tephra.poi.pptx;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShape;
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
     * 解析数据并添加到PPTx元素。
     *
     * @param xmlSlideShow PPTx实例。
     * @param xslfSlide    Slide实例。
     * @param object       数据。
     * @return 如果解析成功则返回true；否则返回false。
     */
    boolean parse(XMLSlideShow xmlSlideShow, XSLFSlide xslfSlide, JSONObject object);

    /**
     * 解析PPTx元素数据。
     *
     * @param object    解析后的数据。
     * @param xslfShape 要解析的PPTx元素。
     * @return 如果解析成功则返回true；否则返回false。
     */
    boolean parse(JSONObject object, XSLFShape xslfShape);
}
