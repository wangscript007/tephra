package org.lpw.tephra.office.pptx.parser;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xslf.usermodel.XSLFSimpleShape;
import org.lpw.tephra.office.pptx.MediaWriter;
import org.springframework.stereotype.Component;

/**
 * @author lpw
 */
@Component("tephra.office.pptx.parser.geometry")
public class GeometryParserImpl implements Parser {
    @Override
    public int getSort() {
        return 6;
    }

    @Override
    public void parse(XSLFSimpleShape xslfSimpleShape, MediaWriter mediaWriter, JSONObject shape) {
//        if (!(xslfShape instanceof XSLFSimpleShape))
//            return;
//
//        XSLFSimpleShape xslfSimpleShape = (XSLFSimpleShape) xslfShape;
//        CustomGeometry customGeometry = xslfSimpleShape.getGeometry();
//        if (customGeometry == null)
//            return;
//
//        System.out.println("##############################################################");
//        System.out.println(xslfShape.getXmlObject());
    }
}
