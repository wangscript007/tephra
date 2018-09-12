package org.lpw.tephra.office.pptx.parser;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSimpleShape;
import org.lpw.tephra.office.pptx.ReaderContext;
import org.lpw.tephra.office.pptx.WriterContext;
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
    public void parseShape(ReaderContext readerContext, XSLFSimpleShape xslfSimpleShape, JSONObject shape) {
        if (xslfSimpleShape.getRotation() == 0.0D)
            return;

        int rotation = numeric.toInt(xslfSimpleShape.getRotation());
        shape.put("rotation", rotation < 0 ? (360 + rotation) : rotation);
    }

    @Override
    public XSLFShape createShape(WriterContext writerContext, JSONObject shape) {
        return null;
    }

    @Override
    public void parseShape(WriterContext writerContext, XSLFSimpleShape xslfSimpleShape, JSONObject shape) {
        if (shape.containsKey("rotation"))
            xslfSimpleShape.setRotation(shape.getDoubleValue("rotation"));
    }
}
