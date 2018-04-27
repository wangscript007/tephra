package org.lpw.tephra.office.pptx.parser;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.xslf.usermodel.XSLFSimpleShape;
import org.lpw.tephra.office.OfficeHelper;
import org.lpw.tephra.office.pptx.MediaWriter;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.awt.Color;

/**
 * @author lpw
 */
@Component("tephra.office.pptx.parser.fill")
public class FillParserImpl implements Parser {
    @Inject
    private OfficeHelper officeHelper;
    @Inject
    private FillXmlParser fillXmlParser;

    @Override
    public int getSort() {
        return 2;
    }

    @Override
    public void parse(XSLFSimpleShape xslfSimpleShape, MediaWriter mediaWriter, JSONObject shape) {
        JSONObject fill = new JSONObject();
        color(xslfSimpleShape, fill);
        texture(xslfSimpleShape, mediaWriter, fill);
        if (!fill.isEmpty())
            shape.put("fill", fill);
    }

    private void color(XSLFSimpleShape xslfSimpleShape, JSONObject fill) {
        Color fillColor = xslfSimpleShape.getFillColor();
        if (fillColor == null)
            return;

        fill.put("color", officeHelper.colorToJson(fillColor));
    }

    private void texture(XSLFSimpleShape xslfSimpleShape, MediaWriter mediaWriter, JSONObject fill) {
        PaintStyle paintStyle = xslfSimpleShape.getFillStyle().getPaint();
        if (!(paintStyle instanceof PaintStyle.TexturePaint))
            return;

        JSONObject texture = new JSONObject();
        PaintStyle.TexturePaint texturePaint = (PaintStyle.TexturePaint) paintStyle;
        texture.put("contentType", texturePaint.getContentType());
        texture.put("alpha", texturePaint.getAlpha() / 100000.0D);
        texture.put("uri", mediaWriter.write(MediaWriter.Type.Image, texturePaint.getContentType(), texturePaint.getImageData()));
        fillXmlParser.putFillRect(xslfSimpleShape.getXmlObject().toString(), texture);
        fill.put("texture", texture);
    }
}
