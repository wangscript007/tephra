package org.lpw.tephra.poi.pptx;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.lpw.tephra.poi.StreamWriter;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * @author lpw
 */
@Component("tephra.poi.pptx.background")
public class BackgroundParserImpl implements Parser {
    @Inject
    private ParserHelper parserHelper;

    @Override
    public String getType() {
        return "background";
    }

    @Override
    public boolean parse(XMLSlideShow xmlSlideShow, XSLFSlide xslfSlide, JSONObject object) {
        if (!object.containsKey("color"))
            return false;

        xslfSlide.getXmlObject().getCSld().addNewBg();
        xslfSlide.getBackground().setFillColor(parserHelper.getColor(object, "color"));

        return true;
    }

    @Override
    public boolean parse(JSONObject object, XSLFShape xslfShape, StreamWriter streamWriter) {
        return false;
    }
}
