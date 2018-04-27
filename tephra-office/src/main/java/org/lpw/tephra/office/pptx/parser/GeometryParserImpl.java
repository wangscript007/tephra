package org.lpw.tephra.office.pptx.parser;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.sl.usermodel.StrokeStyle;
import org.apache.poi.xslf.usermodel.XSLFSimpleShape;
import org.lpw.tephra.office.OfficeHelper;
import org.lpw.tephra.office.pptx.MediaWriter;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.awt.Color;

/**
 * @author lpw
 */
@Component("tephra.office.pptx.parser.geometry")
public class GeometryParserImpl implements Parser {
    @Inject
    private OfficeHelper officeHelper;
    @Inject
    private GeometryXmlParser geometryXmlParser;

    @Override
    public int getSort() {
        return 6;
    }

    @Override
    public void parse(XSLFSimpleShape xslfSimpleShape, MediaWriter mediaWriter, JSONObject shape) {
        JSONObject geometry = new JSONObject();
        parseLine(xslfSimpleShape, geometry);
        parseFill(xslfSimpleShape, mediaWriter, geometry);
        if (!geometry.isEmpty()) {
            geometry.put("type", xslfSimpleShape.getShapeType() == null ? "rect" : format(xslfSimpleShape.getShapeType().toString()));
            shape.put("geometry", geometry);
        }
    }

    private void parseLine(XSLFSimpleShape xslfSimpleShape, JSONObject geometry) {
        if (xslfSimpleShape.getLineWidth() == 0.0D || xslfSimpleShape.getLineColor() == null)
            return;

        JSONObject line = new JSONObject();
        line.put("width", officeHelper.pointToPixel(xslfSimpleShape.getLineWidth()));
        line.put("style", getLineStyle(xslfSimpleShape.getLineDash()));
        line.put("color", officeHelper.colorToJson(xslfSimpleShape.getLineColor()));
        parseAlpha(xslfSimpleShape.getStrokeStyle().getPaint(), line.getJSONObject("color"));
        geometry.put("line", line);
    }

    private String getLineStyle(StrokeStyle.LineDash lineDash) {
        return lineDash == null ? "solid" : format(lineDash.toString());
    }

    private String format(String string) {
        return string.toLowerCase().replace('_', '-');
    }

    private void parseAlpha(PaintStyle paintStyle, JSONObject color) {
        if (!(paintStyle instanceof PaintStyle.SolidPaint))
            return;

        int alpha = ((PaintStyle.SolidPaint) paintStyle).getSolidColor().getAlpha();
        if (alpha > -1)
            color.put("alpha", officeHelper.fromPercent(255, alpha));
    }

    private void parseFill(XSLFSimpleShape xslfSimpleShape, MediaWriter mediaWriter, JSONObject geometry) {
        JSONObject fill = new JSONObject();
        parseColor(xslfSimpleShape, fill);
        parseTexture(xslfSimpleShape, mediaWriter, fill);
        if (!fill.isEmpty())
            geometry.put("fill", fill);

    }

    private void parseColor(XSLFSimpleShape xslfSimpleShape, JSONObject fill) {
        Color fillColor = xslfSimpleShape.getFillColor();
        if (fillColor != null)
            fill.put("color", officeHelper.colorToJson(fillColor));
    }

    private void parseTexture(XSLFSimpleShape xslfSimpleShape, MediaWriter mediaWriter, JSONObject fill) {
        PaintStyle paintStyle = xslfSimpleShape.getFillStyle().getPaint();
        if (!(paintStyle instanceof PaintStyle.TexturePaint))
            return;

        JSONObject texture = new JSONObject();
        PaintStyle.TexturePaint texturePaint = (PaintStyle.TexturePaint) paintStyle;
        texture.put("contentType", texturePaint.getContentType());
        texture.put("alpha", texturePaint.getAlpha() / 100000.0D);
        texture.put("uri", mediaWriter.write(MediaWriter.Type.Image, texturePaint.getContentType(), texturePaint.getImageData()));
        geometryXmlParser.putFillRect(xslfSimpleShape.getXmlObject().toString(), texture);
        fill.put("texture", texture);
    }
}
