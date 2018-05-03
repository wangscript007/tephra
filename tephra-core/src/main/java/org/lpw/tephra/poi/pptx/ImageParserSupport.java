package org.lpw.tephra.poi.pptx;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.xslf.usermodel.XSLFAutoShape;
import org.apache.poi.xslf.usermodel.XSLFPictureData;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;
import org.apache.poi.xslf.usermodel.XSLFRelation;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.lpw.tephra.util.Numeric;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlip;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlipFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.STBlipCompression;
import org.openxmlformats.schemas.presentationml.x2006.main.CTShape;

import javax.inject.Inject;

/**
 * @author lpw
 */
public class ImageParserSupport {
    @Inject
    Numeric numeric;
    @Inject
    ParserHelper parserHelper;

    void parse(XSLFSlide xslfSlide, XSLFPictureData xslfPictureData, JSONObject object) {
        if (!object.containsKey("alpha")) {
            parseImage(xslfSlide, xslfPictureData, object);

            return;
        }

        double alpha = object.getDoubleValue("alpha");
        if (alpha >= 1.0D) {
            parseImage(xslfSlide, xslfPictureData, object);

            return;
        }

        PackagePart packagePart = xslfPictureData.getPackagePart();
        POIXMLDocumentPart.RelationPart relationPart = xslfSlide.addRelation(null, XSLFRelation.IMAGES,
                new XSLFPictureData(packagePart));

        XSLFAutoShape xslfAutoShape = xslfSlide.createAutoShape();
        CTShape ctShape = (CTShape) xslfAutoShape.getXmlObject();
        CTBlipFillProperties ctBlipFillProperties = ctShape.getSpPr().addNewBlipFill();
        CTBlip ctBlip = ctBlipFillProperties.addNewBlip();
        ctBlip.setEmbed(relationPart.getRelationship().getId());
        ctBlip.setCstate(STBlipCompression.PRINT);
        ctBlip.addNewAlphaModFix().setAmt(numeric.toInt(alpha * 100000));
        ctBlipFillProperties.addNewSrcRect();
        ctBlipFillProperties.addNewStretch().addNewFillRect();
        xslfAutoShape.setAnchor(parserHelper.getRectangle(object));
        parserHelper.rotate(xslfAutoShape, object);
    }

    private void parseImage(XSLFSlide xslfSlide, XSLFPictureData xslfPictureData, JSONObject object) {
        XSLFPictureShape xslfPictureShape = xslfSlide.createPicture(xslfPictureData);
        xslfPictureShape.setAnchor(parserHelper.getRectangle(object));
        parserHelper.rotate(xslfPictureShape, object);
    }
}
