package org.lpw.tephra.office.pptx.parser;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xslf.usermodel.XSLFSimpleShape;
import org.lpw.tephra.office.pptx.MediaWriter;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * @author lpw
 */
@Component("tephra.office.pptx.parser.filp")
public class FlipParserImpl implements Parser {
    @Inject
    private FlipXmlParser flipXmlParser;

    @Override
    public int getSort() {
        return 3;
    }

    @Override
    public void parse(XSLFSimpleShape xslfSimpleShape, MediaWriter mediaWriter, JSONObject shape) {
        JSONObject flip = new JSONObject();
        if (xslfSimpleShape.getFlipHorizontal())
            flip.put("horizontal", true);
        if (xslfSimpleShape.getFlipVertical())
            flip.put("vertical", true);
        flipXmlParser.putScene3d(xslfSimpleShape.getXmlObject().toString(), flip);
        if (!flip.isEmpty())
            shape.put("flip", flip);
    }
}
