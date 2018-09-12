package org.lpw.tephra.office.pptx.parser;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xslf.usermodel.XSLFGraphicFrame;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSimpleShape;
import org.lpw.tephra.office.OfficeHelper;
import org.lpw.tephra.office.pptx.ReaderContext;
import org.lpw.tephra.office.pptx.WriterContext;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.awt.geom.Rectangle2D;

/**
 * @author lpw
 */
@Component("tephra.office.pptx.parser.anchor")
public class AnchorImpl implements Simple, Graphic, Anchor {
    @Inject
    private OfficeHelper officeHelper;

    @Override
    public int getSort() {
        return 0;
    }

    @Override
    public void parseShape(ReaderContext readerContext, XSLFSimpleShape xslfSimpleShape, JSONObject shape) {
        parse(xslfSimpleShape.getAnchor(), shape);
    }

    @Override
    public void parseShape(ReaderContext readerContext, XSLFGraphicFrame xslfGraphicFrame, JSONObject shape) {
        parse(xslfGraphicFrame.getAnchor(), shape);
    }

    @Override
    public void parse(Rectangle2D rectangle2D, JSONObject shape) {
        JSONObject anchor = new JSONObject();
        anchor.put("x", officeHelper.pointToPixel(rectangle2D.getX()));
        anchor.put("y", officeHelper.pointToPixel(rectangle2D.getY()));
        anchor.put("width", officeHelper.pointToPixel(rectangle2D.getWidth()));
        anchor.put("height", officeHelper.pointToPixel(rectangle2D.getHeight()));
        shape.put("anchor", anchor);
    }

    @Override
    public XSLFShape createShape(WriterContext writerContext, JSONObject shape) {
        return null;
    }

    @Override
    public void parseShape(WriterContext writerContext, XSLFSimpleShape xslfSimpleShape, JSONObject shape) {
        if (!shape.containsKey("anchor"))
            return;

        JSONObject anchor = shape.getJSONObject("anchor");
        xslfSimpleShape.setAnchor(new Rectangle2D.Double(
                officeHelper.pixelToPoint(anchor.getIntValue("x")),
                officeHelper.pixelToPoint(anchor.getIntValue("y")),
                officeHelper.pixelToPoint(anchor.getIntValue("width")),
                officeHelper.pixelToPoint(anchor.getIntValue("height"))));
    }
}
