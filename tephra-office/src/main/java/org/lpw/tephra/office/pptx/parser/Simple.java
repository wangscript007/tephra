package org.lpw.tephra.office.pptx.parser;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xslf.usermodel.XSLFSimpleShape;
import org.lpw.tephra.office.pptx.ReaderContext;
import org.lpw.tephra.office.pptx.WriterContext;

/**
 * 简单形状解析器。
 *
 * @author lpw
 */
public interface Simple extends Creater {
    /**
     * 获取处理顺序。
     *
     * @return 处理顺序。
     */
    int getSort();

    /**
     * 解析形状。
     *
     * @param readerContext   读上下文。
     * @param xslfSimpleShape 形状。
     * @param shape           解析数据。
     */
    void parseShape(ReaderContext readerContext, XSLFSimpleShape xslfSimpleShape, JSONObject shape);

    /**
     * 解析数据。
     *
     * @param writerContext   写上下文。
     * @param xslfSimpleShape 形状。
     * @param shape           解析数据。
     */
    void parseShape(WriterContext writerContext, XSLFSimpleShape xslfSimpleShape, JSONObject shape);
}
