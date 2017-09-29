package org.lpw.tephra.poi.pptx;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xslf.usermodel.XSLFSimpleShape;

import java.awt.Rectangle;

/**
 * @author lpw
 */
class ParserSupport {
    Rectangle getRectangle(JSONObject object) {
        return new Rectangle(object.getIntValue("x"), object.getIntValue("y"), object.getIntValue("width"), object.getIntValue("height"));
    }

    void rotate(XSLFSimpleShape xslfSimpleShape,JSONObject object){
        if (object.containsKey("rotation"))
            xslfSimpleShape.setRotation(object.getDoubleValue("rotation"));
    }
}
