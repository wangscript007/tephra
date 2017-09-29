package org.lpw.tephra.poi.pptx;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.sl.usermodel.Insets2D;
import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextBox;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.lpw.tephra.util.Numeric;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.awt.Color;

/**
 * @author lpw
 */
@Component("tephra.poi.pptx.text")
public class TextParserImpl extends ParserSupport implements Parser {
    @Inject
    private Numeric numeric;

    @Override
    public String getType() {
        return "text";
    }

    @Override
    public void parse(XMLSlideShow xmlSlideShow, XSLFSlide xslfSlide, JSONObject object) {
        XSLFTextBox xslfTextBox = xslfSlide.createTextBox();
        xslfTextBox.setAnchor(getRectangle(object));
        xslfTextBox.setInsets(new Insets2D(-15.0D, 0.0D, 0.0D, 0.0D));
        if (object.containsKey("rotation"))
            xslfTextBox.setRotation(object.getDoubleValue("rotation"));
        XSLFTextParagraph xslfTextParagraph = xslfTextBox.addNewTextParagraph();
        align(xslfTextParagraph, object);
        XSLFTextRun xslfTextRun = xslfTextParagraph.addNewTextRun();
        xslfTextRun.setText(object.getString("text"));
        font(xslfTextRun, object);
        color(xslfTextRun, object);
        if (hasTrue(object, "bold"))
            xslfTextRun.setBold(true);
        if (hasTrue(object, "underline"))
            xslfTextRun.setUnderlined(true);
        if (hasTrue(object, "italic"))
            xslfTextRun.setItalic(true);
        if (object.containsKey("spacing"))
            xslfTextRun.setCharacterSpacing(numeric.toDouble(object.getString("spacing")));
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

    private void font(XSLFTextRun xslfTextRun, JSONObject object) {
        if (!object.containsKey("font"))
            return;

        JSONObject font = object.getJSONObject("font");
        if (font.containsKey("family"))
            xslfTextRun.setFontFamily(font.getString("family"));
        if (font.containsKey("size"))
            xslfTextRun.setFontSize(numeric.toDouble(font.getString("size")));
    }

    private void color(XSLFTextRun xslfTextRun, JSONObject object) {
        if (!object.containsKey("color"))
            return;

        String color = object.getString("color");
        int[] ns;
        if (color.charAt(0) == '#') {
            String[] array = new String[3];
            boolean full = color.length() == 7;
            for (int i = 0; i < array.length; i++)
                array[i] = full ? color.substring(2 * i + 1, 2 * (i + 1) + 1) : (color.substring(i + 1, i + 2) + color.substring(i + 1, i + 2));
            ns = new int[3];
            for (int i = 0; i < ns.length; i++)
                ns[i] = Integer.parseInt(array[i], 16);
        } else
            ns = numeric.toInts(color);
        xslfTextRun.setFontColor(new Color(ns[0], ns[1], ns[2]));
    }

    private boolean hasTrue(JSONObject object, String key) {
        return object.containsKey(key) && object.getBoolean(key);
    }
}
