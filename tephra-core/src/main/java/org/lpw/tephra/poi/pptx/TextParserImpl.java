package org.lpw.tephra.poi.pptx;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.sl.usermodel.Insets2D;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextBox;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.springframework.stereotype.Component;

/**
 * @author lpw
 */
@Component("tephra.poi.pptx.text")
public class TextParserImpl extends ParserSupport implements Parser {
    @Override
    public String getType() {
        return "text";
    }

    @Override
    public void parse(XMLSlideShow xmlSlideShow, XSLFSlide xslfSlide, JSONObject object) {
        XSLFTextBox xslfTextBox = xslfSlide.createTextBox();
        xslfTextBox.setInsets(new Insets2D(-15.0D, 0.0D, 0.0D, 0.0D));
        XSLFTextParagraph xslfTextParagraph = xslfTextBox.addNewTextParagraph();
        XSLFTextRun xslfTextRun = xslfTextParagraph.addNewTextRun();
        xslfTextRun.setText(object.getString("text"));
        xslfTextBox.setAnchor(getRectangle(object));
    }
}
