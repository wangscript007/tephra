package org.lpw.tephra.office.pptx.parser;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xslf.usermodel.XSLFShadow;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSimpleShape;
import org.lpw.tephra.office.OfficeHelper;
import org.lpw.tephra.office.pptx.ReaderContext;
import org.lpw.tephra.office.pptx.WriterContext;
import org.openxmlformats.schemas.drawingml.x2006.main.CTEffectList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOuterShadowEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSRgbColor;
import org.openxmlformats.schemas.drawingml.x2006.main.STRectAlignment;
import org.openxmlformats.schemas.presentationml.x2006.main.CTShape;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.awt.Color;

/**
 * @author lpw
 */
@Component("tephra.office.pptx.parser.shadow")
public class ShadowImpl implements Simple {
    @Inject
    private OfficeHelper officeHelper;

    @Override
    public int getSort() {
        return 3;
    }

    @Override
    public void parseShape(ReaderContext readerContext, XSLFSimpleShape xslfSimpleShape, JSONObject shape) {
        if (xslfSimpleShape.getShadow() == null)
            return;

        XSLFShadow xslfShadow = xslfSimpleShape.getShadow();
        JSONObject shadow = new JSONObject();
        shadow.put("angle", xslfShadow.getAngle());
        shadow.put("blur", officeHelper.pointToPixel(xslfShadow.getBlur()));
        shadow.put("distance", officeHelper.pointToPixel(xslfShadow.getDistance()));
        shadow.put("color", officeHelper.colorToJson(xslfShadow.getFillColor()));
        String type = xslfShadow.getXmlObject().getDomNode().getNodeName();
        shadow.put("type", type.substring(2, type.length() - 4));
        shape.put("shadow", shadow);
    }

    @Override
    public XSLFShape createShape(WriterContext writerContext, JSONObject shape) {
        return null;
    }

    @Override
    public void parseShape(WriterContext writerContext, XSLFSimpleShape xslfSimpleShape, JSONObject shape) {
        if (!shape.containsKey("shadow"))
            return;

        JSONObject shadow = shape.getJSONObject("shadow");
        CTShape ctShape = (CTShape) xslfSimpleShape.getXmlObject();
        CTEffectList ctEffectList = ctShape.getSpPr().getEffectLst();
        if (ctEffectList == null)
            ctEffectList = ctShape.getSpPr().addNewEffectLst();
        switch (shadow.getString("type")) {
            case "inner":
                return;
            case "outer":
                CTOuterShadowEffect ctOuterShadowEffect = ctEffectList.addNewOuterShdw();
                ctOuterShadowEffect.setAlgn(STRectAlignment.Enum.forString("ctr"));
                ctOuterShadowEffect.setBlurRad(officeHelper.pixelToEmu(shadow.getIntValue("blur")));
                ctOuterShadowEffect.setDist(officeHelper.pixelToEmu(shadow.getIntValue("distance")));
                ctOuterShadowEffect.setDir(shadow.getIntValue("angle") * 60000);
                Color color = officeHelper.jsonToColor(shadow.getJSONObject("color"));
                CTSRgbColor ctsRgbColor = ctOuterShadowEffect.addNewSrgbClr();
                ctsRgbColor.addNewAlpha().setVal(officeHelper.toPercent(color.getAlpha() / 256.0D));
                ctsRgbColor.addNewRed().setVal(color.getRed());
                ctsRgbColor.addNewGreen().setVal(color.getGreen());
                ctsRgbColor.addNewBlue().setVal(color.getBlue());

                return;
            default:
        }
    }
}
