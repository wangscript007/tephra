package org.lpw.tephra.office.pptx.parser;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xslf.usermodel.XSLFShadow;
import org.apache.poi.xslf.usermodel.XSLFSimpleShape;
import org.lpw.tephra.office.OfficeHelper;
import org.lpw.tephra.office.MediaWriter;
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
    public void parse(XSLFSimpleShape xslfSimpleShape, MediaWriter mediaWriter, JSONObject shape, boolean layout) {
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
}
