package org.lpw.tephra.office.pptx.parser;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xslf.usermodel.XSLFSimpleShape;
import org.lpw.tephra.office.pptx.MediaWriter;
import org.lpw.tephra.util.Numeric;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * @author lpw
 */
@Component("tephra.office.pptx.parser.rotation")
public class RotationImpl implements Simple {
    @Inject
    private Numeric numeric;

    @Override
    public int getSort() {
        return 2;
    }

    @Override
    public void parse(XSLFSimpleShape xslfSimpleShape, MediaWriter mediaWriter, JSONObject shape, boolean layout) {
        if (xslfSimpleShape.getRotation() == 0.0D)
            return;

        int rotation = numeric.toInt(xslfSimpleShape.getRotation());
        shape.put("rotation", rotation < 0 ? (360 + rotation) : rotation);
    }
}
