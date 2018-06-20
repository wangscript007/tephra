package org.lpw.tephra.office.pptx.parser;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShadow;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSimpleShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.lpw.tephra.office.MediaReader;
import org.lpw.tephra.office.MediaWriter;
import org.lpw.tephra.office.OfficeHelper;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * @author lpw
 */
@Component("tephra.office.pptx.parser.shadow")
public class ShadowImpl implements Simple {
    @Inject
    private OfficeHelper officeHelper;
    @Inject
    private Anchor anchor;

    @Override
    public int getSort() {
        return 3;
    }

    @Override
    public void parseShape(XSLFSimpleShape xslfSimpleShape, MediaWriter mediaWriter, JSONObject shape, boolean layout) {
        if (xslfSimpleShape.getShadow() == null)
            return;

        XSLFShadow xslfShadow = xslfSimpleShape.getShadow();
        JSONObject shadow = new JSONObject();
        anchor.parse(xslfShadow.getAnchor(), shadow);
        shadow.put("angle", xslfShadow.getAngle());
        shadow.put("blur", officeHelper.pointToPixel(xslfShadow.getBlur()));
        shadow.put("distance", officeHelper.pointToPixel(xslfShadow.getDistance()));
        shadow.put("color", officeHelper.colorToJson(xslfShadow.getFillColor()));
        shape.put("shadow", shadow);
    }

    @Override
    public XSLFShape createShape(XMLSlideShow xmlSlideShow, XSLFSlide xslfSlide, MediaReader mediaReader, JSONObject shape) {
        return null;
    }

    @Override
    public void parseToShape(XSLFSimpleShape xslfSimpleShape, MediaReader mediaReader, JSONObject shape) {
        if (!shape.containsKey("shadow"))
            return;

        JSONObject shadow = shape.getJSONObject("shadow");
        XSLFShadow xslfShadow = xslfSimpleShape.getShadow();
        System.out.println("##########################" + xslfShadow+";"+shadow);
    }
}
