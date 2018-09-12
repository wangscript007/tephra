package org.lpw.tephra.office.pptx.parser;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xslf.usermodel.XSLFGraphicFrame;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSimpleShape;
import org.lpw.tephra.office.pptx.ReaderContext;
import org.lpw.tephra.office.pptx.WriterContext;

/**
 * 解析器。
 *
 * @author lpw
 */
public interface Parser {
    /**
     * 解析形状。
     *
     * @param readerContext   读上下文。
     * @param xslfSimpleShape 形状。
     * @param shape           解析数据。
     */
    void parseShape(ReaderContext readerContext, XSLFSimpleShape xslfSimpleShape, JSONObject shape);

    /**
     * 解析形状。
     *
     * @param readerContext    读上下文。
     * @param xslfGraphicFrame 形状。
     * @param shape            解析数据。
     */
    void parseShape(ReaderContext readerContext, XSLFGraphicFrame xslfGraphicFrame, JSONObject shape);

    /**
     * 创建形状。
     *
     * @param writerContext 写上下文。
     * @param shape         形状数据。
     * @return 形状；如果创建失败则返回null。
     */
    XSLFShape createShape(WriterContext writerContext, JSONObject shape);

    /**
     * 解析数据。
     *
     * @param writerContext   写上下文。
     * @param xslfSimpleShape 形状。
     * @param shape           解析数据。
     */
    void parseShape(WriterContext writerContext, XSLFSimpleShape xslfSimpleShape, JSONObject shape);
}
