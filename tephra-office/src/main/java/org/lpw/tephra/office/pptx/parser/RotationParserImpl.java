package org.lpw.tephra.office.pptx.parser;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xslf.usermodel.XSLFSimpleShape;
import org.lpw.tephra.office.pptx.MediaWriter;
import org.springframework.stereotype.Component;

/**
 * @author lpw
 */
@Component("tephra.office.pptx.parser.rotation")
public class RotationParserImpl implements Parser {
    @Override
    public int getSort() {
        return 4;
    }

    @Override
    public void parse(XSLFSimpleShape xslfSimpleShape, MediaWriter mediaWriter, JSONObject shape) {
        if (xslfSimpleShape.getRotation() == 0.0D)
            return;

        shape.put("rotation", xslfSimpleShape.getRotation());
    }
}
