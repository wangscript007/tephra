package org.lpw.tephra.office.pptx.parser;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.sl.draw.DrawPaint;
import org.apache.poi.sl.usermodel.Insets2D;
import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.sl.usermodel.TextShape;
import org.apache.poi.sl.usermodel.VerticalAlignment;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSimpleShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.lpw.tephra.office.MediaReader;
import org.lpw.tephra.office.MediaWriter;
import org.lpw.tephra.office.OfficeHelper;
import org.lpw.tephra.util.Validator;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lpw
 */
@Component("tephra.office.pptx.parser.text")
public class TextImpl implements Simple {
    @Inject
    private Validator validator;
    @Inject
    private OfficeHelper officeHelper;
    private String[] merges = {"horizontalAlign", "fontFamily", "fontSize", "color", "bold", "italic", "underline", "strikethrough",
            "subscript", "superscript"};

    @Override
    public int getSort() {
        return 9;
    }

    @Override
    public void parseShape(XSLFSimpleShape xslfSimpleShape, MediaWriter mediaWriter, JSONObject shape, boolean layout) {
        if (!(xslfSimpleShape instanceof XSLFTextShape))
            return;

        XSLFTextShape xslfTextShape = (XSLFTextShape) xslfSimpleShape;
        JSONArray paragraphs = new JSONArray();
        xslfTextShape.getTextParagraphs().forEach(xslfTextParagraph -> {
            JSONObject paragraph = new JSONObject();
            parseAlign(xslfTextParagraph, paragraph);
            JSONArray words = new JSONArray();
            xslfTextParagraph.getTextRuns().forEach(xslfTextRun -> {
                if (validator.isEmpty(xslfTextRun.getRawText()))
                    return;

                JSONObject word = new JSONObject();
                word.put("fontFamily", xslfTextRun.getFontFamily());
                word.put("fontSize", officeHelper.pointToPixel(xslfTextRun.getFontSize()));
                word.put("bold", xslfTextRun.isBold());
                word.put("italic", xslfTextRun.isItalic());
                word.put("underline", xslfTextRun.isUnderlined());
                word.put("strikethrough", xslfTextRun.isStrikethrough());
                word.put("subscript", xslfTextRun.isSubscript());
                word.put("superscript", xslfTextRun.isSuperscript());
                word.put("word", xslfTextRun.getRawText());
                parseColor(xslfTextRun.getFontColor(), word);
                words.add(word);
            });
            if (words.isEmpty())
                return;

            merge(paragraph, words);
            paragraph.put("words", merge(words));
            paragraphs.add(paragraph);
        });
        if (paragraphs.isEmpty())
            return;

        JSONObject text = new JSONObject();
        text.put("direction", xslfTextShape.getTextDirection().name().toLowerCase());
        parseMargin(xslfTextShape, text);
        parseVerticalAlignment(xslfTextShape, text);
        merge(text, paragraphs);
        text.put("paragraphs", paragraphs);
        text.put("layout", layout);
        shape.put("text", text);
    }

    private void parseMargin(XSLFTextShape xslfTextShape, JSONObject text) {
        Insets2D insets2D = xslfTextShape.getInsets();
        JSONObject margin = new JSONObject();
        margin.put("left", officeHelper.pointToPixel(insets2D.left));
        margin.put("top", officeHelper.pointToPixel(insets2D.top));
        margin.put("right", officeHelper.pointToPixel(insets2D.right));
        margin.put("bottom", officeHelper.pointToPixel(insets2D.bottom));
        text.put("margin", margin);
    }

    private void parseVerticalAlignment(XSLFTextShape xslfTextShape, JSONObject text) {
        switch (xslfTextShape.getVerticalAlignment()) {
            case TOP:
                text.put("verticalAlign", "top");
                return;
            case MIDDLE:
                text.put("verticalAlign", "middle");
                return;
            case BOTTOM:
                text.put("verticalAlign", "bottom");
        }
    }

    private void parseAlign(XSLFTextParagraph xslfTextParagraph, JSONObject paragraph) {
        switch (xslfTextParagraph.getTextAlign()) {
            case LEFT:
                paragraph.put("horizontalAlign", "left");
                return;
            case CENTER:
                paragraph.put("horizontalAlign", "center");
                return;
            case RIGHT:
                paragraph.put("horizontalAlign", "right");
                return;
            case JUSTIFY:
                paragraph.put("horizontalAlign", "justify");
        }
    }

    private void parseColor(PaintStyle paintStyle, JSONObject word) {
        if (paintStyle instanceof PaintStyle.SolidPaint)
            word.put("color", officeHelper.colorToJson(
                    DrawPaint.applyColorTransform(((PaintStyle.SolidPaint) paintStyle).getSolidColor())));
    }

    private void merge(JSONObject object, JSONArray array) {
        if (array.isEmpty())
            return;

        int size = array.size();
        if (size == 1) {
            JSONObject obj = array.getJSONObject(0);
            for (String key : merges)
                if (obj.containsKey(key))
                    object.put(key, obj.remove(key));

            return;
        }

        for (String key : merges) {
            Map<Object, Integer> map = new HashMap<>();
            for (int i = 0; i < size; i++) {
                JSONObject obj = array.getJSONObject(i);
                if (!obj.containsKey(key))
                    break;

                Object value = obj.get(key);
                map.put(value, map.getOrDefault(value, 0) + 1);
            }
            if (map.isEmpty())
                continue;

            Object value = null;
            int count = 0;
            for (Object k : map.keySet()) {
                int v = map.get(k);
                if (v <= count)
                    continue;

                count = v;
                value = k;
            }
            object.put(key, value);
            for (int i = 0; i < size; i++) {
                JSONObject obj = array.getJSONObject(i);
                if (!obj.containsKey(key))
                    break;

                if (obj.get(key).equals(value))
                    obj.remove(key);
            }
        }
    }

    private JSONArray merge(JSONArray words) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0, size = words.size(); i < size; i++) {
            JSONObject word = words.getJSONObject(i);
            if (word.size() > 1)
                return words;

            sb.append(word.getString("word"));
        }

        JSONObject object = new JSONObject();
        object.put("word", sb.toString());
        JSONArray array = new JSONArray();
        array.add(object);

        return array;
    }

    @Override
    public XSLFShape createShape(XMLSlideShow xmlSlideShow, XSLFSlide xslfSlide, MediaReader mediaReader, JSONObject shape) {
        return shape.containsKey("text") ? xslfSlide.createTextBox() : null;
    }

    @Override
    public void parseToShape(XSLFSimpleShape xslfSimpleShape, MediaReader mediaReader, JSONObject shape) {
        if (!shape.containsKey("text"))
            return;

        JSONObject text = shape.getJSONObject("text");
        JSONArray paragraphs = text.getJSONArray("paragraphs");
        XSLFTextShape xslfTextShape = (XSLFTextShape) xslfSimpleShape;
        xslfTextShape.clearText();
        parseInsets(xslfTextShape, text);
        xslfTextShape.setTextDirection(text.containsKey("direction") && text.getString("direction").equals("vertical") ?
                TextShape.TextDirection.VERTICAL : TextShape.TextDirection.HORIZONTAL);
        xslfTextShape.setVerticalAlignment(getVerticalAlign(text));
        for (int i = 0, pSize = paragraphs.size(); i < pSize; i++) {
            JSONObject paragraph = paragraphs.getJSONObject(i);
            JSONArray words = paragraph.getJSONArray("words");
            XSLFTextParagraph xslfTextParagraph = xslfTextShape.addNewTextParagraph();
            xslfTextParagraph.setTextAlign(getHorizontalAlign(text, paragraph));
            for (int j = 0, wSize = words.size(); j < wSize; j++) {
                JSONObject word = words.getJSONObject(j);
                XSLFTextRun xslfTextRun = xslfTextParagraph.addNewTextRun();
                xslfTextRun.setFontFamily(getString(text, paragraph, word, "fontFamily"));
                xslfTextRun.setFontSize(officeHelper.pixelToPoint(getFontSize(text, paragraph, word)));
                JSONObject color = getColor(text, paragraph, word);
                if (color != null)
                    xslfTextRun.setFontColor(officeHelper.jsonToColor(color));
                if (hasTrue(text, paragraph, word, "bold"))
                    xslfTextRun.setBold(true);
                if (hasTrue(text, paragraph, word, "italic"))
                    xslfTextRun.setItalic(true);
                if (hasTrue(text, paragraph, word, "underline"))
                    xslfTextRun.setUnderlined(true);
                if (hasTrue(text, paragraph, word, "strikethrough"))
                    xslfTextRun.setStrikethrough(true);
                if (hasTrue(text, paragraph, word, "subscript"))
                    xslfTextRun.setSubscript(true);
                if (hasTrue(text, paragraph, word, "superscript"))
                    xslfTextRun.setSuperscript(true);
                if (!hasTrue(text, paragraph, word, "layout"))
                    xslfTextRun.setText(word.getString("word"));
            }
        }
    }

    private void parseInsets(XSLFTextShape xslfTextShape, JSONObject text) {
        if (!text.containsKey("margin"))
            return;

        JSONObject margin = text.getJSONObject("margin");
        xslfTextShape.setInsets(new Insets2D(officeHelper.pixelToPoint(margin.getIntValue("top")),
                officeHelper.pixelToPoint(margin.getIntValue("left")),
                officeHelper.pixelToPoint(margin.getIntValue("bottom")),
                officeHelper.pixelToPoint(margin.getIntValue("right"))));
    }

    private VerticalAlignment getVerticalAlign(JSONObject text) {
        if (!text.containsKey("verticalAlign"))
            return null;

        switch (text.getString("verticalAlign")) {
            case "top":
                return VerticalAlignment.TOP;
            case "middle":
                return VerticalAlignment.MIDDLE;
            default:
                return VerticalAlignment.BOTTOM;
        }
    }

    private TextParagraph.TextAlign getHorizontalAlign(JSONObject text, JSONObject paragraph) {
        String horizontalAlign = getString(text, paragraph, null, "horizontalAlign");
        if (horizontalAlign == null)
            return null;

        switch (horizontalAlign) {
            case "left":
                return TextParagraph.TextAlign.LEFT;
            case "center":
                return TextParagraph.TextAlign.CENTER;
            case "right":
                return TextParagraph.TextAlign.RIGHT;
            default:
                return TextParagraph.TextAlign.JUSTIFY;
        }
    }

    private String getString(JSONObject text, JSONObject paragraph, JSONObject word, String key) {
        if (word != null && word.containsKey(key))
            return word.getString(key);

        if (paragraph.containsKey(key))
            return paragraph.getString(key);

        if (text.containsKey(key))
            return text.getString(key);

        return null;
    }

    private int getFontSize(JSONObject text, JSONObject paragraph, JSONObject word) {
        if (word.containsKey("fontSize"))
            return word.getIntValue("fontSize");

        if (paragraph.containsKey("fontSize"))
            return paragraph.getIntValue("fontSize");

        if (text.containsKey("fontSize"))
            return text.getIntValue("fontSize");

        return 0;
    }

    private JSONObject getColor(JSONObject text, JSONObject paragraph, JSONObject word) {
        if (word.containsKey("color"))
            return word.getJSONObject("color");

        if (paragraph.containsKey("color"))
            return paragraph.getJSONObject("color");

        if (text.containsKey("color"))
            return text.getJSONObject("color");

        return null;
    }

    private boolean hasTrue(JSONObject text, JSONObject paragraph, JSONObject word, String key) {
        if (word.containsKey(key))
            return word.getBooleanValue(key);

        if (paragraph.containsKey(key))
            return paragraph.getBooleanValue(key);

        if (text.containsKey(key))
            return text.getBooleanValue(key);

        return false;
    }
}
