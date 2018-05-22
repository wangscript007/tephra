package org.lpw.tephra.office.pptx.parser;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.sl.draw.DrawPaint;
import org.apache.poi.sl.usermodel.Insets2D;
import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.xslf.usermodel.XSLFSimpleShape;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.lpw.tephra.office.OfficeHelper;
import org.lpw.tephra.office.pptx.MediaWriter;
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
    public void parse(XSLFSimpleShape xslfSimpleShape, MediaWriter mediaWriter, JSONObject shape, boolean layout) {
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
            word.put("color", officeHelper.colorToJson(DrawPaint.applyColorTransform(((PaintStyle.SolidPaint) paintStyle).getSolidColor())));
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
}
