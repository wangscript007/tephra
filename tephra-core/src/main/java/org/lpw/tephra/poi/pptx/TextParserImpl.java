package org.lpw.tephra.poi.pptx;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.sl.usermodel.Insets2D;
import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextBox;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.lpw.tephra.util.Json;
import org.lpw.tephra.util.Numeric;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.awt.Color;

/**
 * @author lpw
 */
@Component("tephra.poi.pptx.text")
public class TextParserImpl implements Parser {
    @Inject
    private Numeric numeric;
    @Inject
    private Json json;
    @Inject
    private ParserHelper parserHelper;

    @Override
    public String getType() {
        return "text";
    }

    @Override
    public boolean parse(XMLSlideShow xmlSlideShow, XSLFSlide xslfSlide, JSONObject object) {
        XSLFTextBox xslfTextBox = xslfSlide.createTextBox();
        xslfTextBox.clearText();
        xslfTextBox.setAnchor(parserHelper.getRectangle(object));
        xslfTextBox.setInsets(new Insets2D(0.0D, 0.0D, 0.0D, 0.0D));
        parserHelper.rotate(xslfTextBox, object);
        XSLFTextParagraph xslfTextParagraph = xslfTextBox.addNewTextParagraph();
        align(xslfTextParagraph, object);
        if (object.containsKey("texts")) {
            JSONArray texts = object.getJSONArray("texts");
            for (int i = 0, size = texts.size(); i < size; i++)
                add(xslfTextParagraph, object, texts.getJSONObject(i));
        } else if (object.containsKey("text"))
            add(xslfTextParagraph, object, new JSONObject());

        return true;
    }

    private void align(XSLFTextParagraph xslfTextParagraph, JSONObject object) {
        if (!object.containsKey("align"))
            return;

        String align = object.getString("align");
        if (align.equals("left"))
            xslfTextParagraph.setTextAlign(TextParagraph.TextAlign.LEFT);
        else if (align.equals("center"))
            xslfTextParagraph.setTextAlign(TextParagraph.TextAlign.CENTER);
        else if (align.equals("right"))
            xslfTextParagraph.setTextAlign(TextParagraph.TextAlign.RIGHT);
    }

    private void add(XSLFTextParagraph xslfTextParagraph, JSONObject object, JSONObject child) {
        XSLFTextRun xslfTextRun = xslfTextParagraph.addNewTextRun();
        xslfTextRun.setText(child.containsKey("text") ? child.getString("text") : object.getString("text"));
        font(xslfTextParagraph, xslfTextRun, object, child);
        color(xslfTextRun, object, child);
        if (json.hasTrue(object, "bold") || json.hasTrue(child, "bold"))
            xslfTextRun.setBold(true);
        if (json.hasTrue(object, "underline") || json.hasTrue(child, "underline"))
            xslfTextRun.setUnderlined(true);
        if (json.hasTrue(object, "italic") || json.hasTrue(child, "italic"))
            xslfTextRun.setItalic(true);
        if (object.containsKey("spacing") || child.containsKey("spacing"))
            xslfTextRun.setCharacterSpacing((child.containsKey("spacing") ? child : object).getDoubleValue("spacing"));
    }

    private void font(XSLFTextParagraph xslfTextParagraph, XSLFTextRun xslfTextRun, JSONObject object, JSONObject child) {
        if (!object.containsKey("font") && !child.containsKey("font"))
            return;

        JSONObject font = (child.containsKey("font") ? child : object).getJSONObject("font");
        if (font.containsKey("family"))
            xslfTextRun.setFontFamily(font.getString("family"));
        if (font.containsKey("size")) {
            double height = 0.0D;
            if (font.containsKey("height"))
                height = Math.max(0.0D, font.getDoubleValue("height") - 1);
            double size = numeric.toDouble(font.getDoubleValue("size"));
            double space = size * height / 2;
            xslfTextParagraph.setSpaceBefore(space);
            xslfTextParagraph.setSpaceAfter(space);
            xslfTextRun.setFontSize(size);
        }
    }

    private void color(XSLFTextRun xslfTextRun, JSONObject object, JSONObject child) {
        if (!object.containsKey("color") && !child.containsKey("color"))
            return;

        Color color = parserHelper.getColor(child.containsKey("color") ? child : object, "color");
        if (color != null)
            xslfTextRun.setFontColor(color);
    }
}
