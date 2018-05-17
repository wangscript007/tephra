package org.lpw.tephra.office.pptx.parser;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xslf.usermodel.XSLFSimpleShape;
import org.apache.xmlbeans.XmlObject;
import org.lpw.tephra.office.pptx.MediaWriter;
import org.openxmlformats.schemas.drawingml.x2006.main.CTCamera;
import org.openxmlformats.schemas.drawingml.x2006.main.CTScene3D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSphereCoords;
import org.openxmlformats.schemas.presentationml.x2006.main.CTShape;
import org.springframework.stereotype.Component;

/**
 * @author lpw
 */
@Component("tephra.office.pptx.parser.filp")
public class FlipParserImpl implements Parser {
    @Override
    public int getSort() {
        return 1;
    }

    @Override
    public void parse(XSLFSimpleShape xslfSimpleShape, MediaWriter mediaWriter, JSONObject shape, boolean layout) {
        JSONObject flip = new JSONObject();
        CTSphereCoords ctSphereCoords = getScene3D(xslfSimpleShape);
        if (xslfSimpleShape.getFlipHorizontal() || (ctSphereCoords != null && ctSphereCoords.getLat() == 10800000))
            flip.put("horizontal", true);
        if (xslfSimpleShape.getFlipVertical() || (ctSphereCoords != null && ctSphereCoords.getLon() == 10800000))
            flip.put("vertical", true);
        if (!flip.isEmpty())
            shape.put("flip", flip);
    }

    private CTSphereCoords getScene3D(XSLFSimpleShape xslfSimpleShape) {
        XmlObject xmlObject = xslfSimpleShape.getXmlObject();
        if (!(xmlObject instanceof CTShape))
            return null;

        CTScene3D ctScene3D = ((CTShape) xmlObject).getSpPr().getScene3D();
        if (ctScene3D == null)
            return null;

        CTCamera ctCamera = ctScene3D.getCamera();
        if (ctCamera == null)
            return null;

        return ctCamera.getRot();
    }
}
