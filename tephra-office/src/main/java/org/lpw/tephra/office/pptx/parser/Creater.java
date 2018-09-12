package org.lpw.tephra.office.pptx.parser;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.lpw.tephra.office.pptx.WriterContext;

/**
 * 图形创建器。
 *
 * @author lpw
 */
public interface Creater {
    /**
     * 创建形状。
     *
     * @param writerContext 写上下文。
     * @param shape         形状数据。
     * @return 形状；如果创建失败则返回null。
     */
    XSLFShape createShape(WriterContext writerContext, JSONObject shape);
}
