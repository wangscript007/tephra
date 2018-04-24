package org.lpw.tephra.office.pptx.parser;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.sl.usermodel.Shape;
import org.apache.poi.xslf.usermodel.XSLFBackground;
import org.lpw.tephra.office.pptx.MediaWriter;
import org.springframework.stereotype.Component;

import java.awt.Color;

/**
 * @author lpw
 */
@Component("tephra.office.pptx.parser.background")
public class BackgroundParserImpl implements Parser {
    @Override
    public int getSort() {
        return 0;
    }

    @Override
    public void parse(Shape shape, MediaWriter mediaWriter, JSONObject object) {
        if (!(shape instanceof XSLFBackground))
            return;

        XSLFBackground xslfBackground = (XSLFBackground) shape;
        JSONObject background = new JSONObject();
        color(xslfBackground, background);
        texture(xslfBackground, mediaWriter, background);
        if (!background.isEmpty())
            object.put("background", background);
    }

    private void color(XSLFBackground xslfBackground, JSONObject background) {
        Color fillColor = xslfBackground.getFillColor();
        if (fillColor == null)
            return;

        JSONObject color = new JSONObject();
        color.put("red", fillColor.getRed());
        color.put("green", fillColor.getGreen());
        color.put("blue", fillColor.getBlue());
        color.put("alpha", fillColor.getAlpha());
        background.put("color", color);
    }

    private void texture(XSLFBackground xslfBackground, MediaWriter mediaWriter, JSONObject background) {
        PaintStyle paintStyle = xslfBackground.getFillStyle().getPaint();
        if (!(paintStyle instanceof PaintStyle.TexturePaint))
            return;

        JSONObject texture = new JSONObject();
        PaintStyle.TexturePaint texturePaint = (PaintStyle.TexturePaint) paintStyle;
        texture.put("contentType", texturePaint.getContentType());
        texture.put("alpha", texturePaint.getAlpha() / 100000.0D);
        texture.put("uri", mediaWriter.write(MediaWriter.Type.Image, texturePaint.getImageData()));
        background.put("texture", texture);
    }
}
