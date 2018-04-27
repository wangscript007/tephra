package org.lpw.tephra.office.pptx.parser;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.sl.usermodel.StrokeStyle;
import org.apache.poi.xslf.usermodel.XSLFSimpleShape;
import org.lpw.tephra.office.OfficeHelper;
import org.lpw.tephra.office.pptx.MediaWriter;
import org.lpw.tephra.util.Numeric;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * @author lpw
 */
@Component("tephra.office.pptx.parser.border")
public class BorderParserImpl implements Parser {
    @Inject
    private Numeric numeric;
    @Inject
    private OfficeHelper officeHelper;

    @Override
    public int getSort() {
        return 1;
    }

    @Override
    public void parse(XSLFSimpleShape xslfSimpleShape, MediaWriter mediaWriter, JSONObject shape) {
        if (xslfSimpleShape.getLineWidth() == 0.0D)
            return;

        JSONObject border = new JSONObject();
        border.put("width", officeHelper.pointToPixel(xslfSimpleShape.getLineWidth()));
        border.put("style", getStyle(xslfSimpleShape.getLineDash()));
        border.put("color", officeHelper.colorToJson(xslfSimpleShape.getLineColor()));
        putAlpha(xslfSimpleShape.getStrokeStyle().getPaint(), border.getJSONObject("color"));
        shape.put("border", border);
    }

    private String getStyle(StrokeStyle.LineDash lineDash) {
        return lineDash == null ? "solid" : lineDash.toString().toLowerCase().replace('_', '-');
    }

    private void putAlpha(PaintStyle paintStyle, JSONObject color) {
        if (!(paintStyle instanceof PaintStyle.SolidPaint))
            return;

        int alpha = ((PaintStyle.SolidPaint) paintStyle).getSolidColor().getAlpha();
        if (alpha > -1)
            color.put("alpha", officeHelper.fromPercent(255, alpha));
    }
}
