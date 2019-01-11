package org.lpw.tephra.office.pptx.parser;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xslf.usermodel.XSLFSimpleShape;

/**
 * 属性归零/重做接口。
 *
 * @author lpw
 */
public interface Zero {
    /**
     * 归零。
     *
     * @param xslfSimpleShape XSLFSimpleShape对象。
     * @param shape           数据。
     */
    void zero(XSLFSimpleShape xslfSimpleShape, JSONObject shape);

    /**
     * 重做。
     *
     * @param xslfSimpleShape XSLFSimpleShape对象。
     * @param shape           数据。
     */
    void reset(XSLFSimpleShape xslfSimpleShape, JSONObject shape);
}
