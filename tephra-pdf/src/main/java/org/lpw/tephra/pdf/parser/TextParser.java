package org.lpw.tephra.pdf.parser;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.pdfbox.contentstream.operator.color.SetNonStrokingColor;
import org.apache.pdfbox.contentstream.operator.color.SetNonStrokingColorN;
import org.apache.pdfbox.contentstream.operator.color.SetNonStrokingColorSpace;
import org.apache.pdfbox.contentstream.operator.color.SetNonStrokingDeviceCMYKColor;
import org.apache.pdfbox.contentstream.operator.color.SetNonStrokingDeviceGrayColor;
import org.apache.pdfbox.contentstream.operator.color.SetNonStrokingDeviceRGBColor;
import org.apache.pdfbox.contentstream.operator.color.SetStrokingColor;
import org.apache.pdfbox.contentstream.operator.color.SetStrokingColorN;
import org.apache.pdfbox.contentstream.operator.color.SetStrokingColorSpace;
import org.apache.pdfbox.contentstream.operator.color.SetStrokingDeviceCMYKColor;
import org.apache.pdfbox.contentstream.operator.color.SetStrokingDeviceGrayColor;
import org.apache.pdfbox.contentstream.operator.color.SetStrokingDeviceRGBColor;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.apache.pdfbox.util.Matrix;
import org.lpw.tephra.pdf.PdfHelper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lpw
 */
public class TextParser extends PDFTextStripper {
    private PdfHelper pdfHelper;
    private int pageHeight;
    private JSONArray array;
    private JSONArray words;
    private JSONObject word;
    private double width;
    private TextPosition prevTextPosition;
    private JSONObject anchor;
    private String[] merges = {"horizontalAlign", "fontFamily", "fontSize", "color", "bold", "italic", "underline", "strikethrough",
            "subscript", "superscript"};

    public TextParser(PdfHelper pdfHelper, int pageHeight) throws IOException {
        super();

        this.pdfHelper = pdfHelper;
        this.pageHeight = pageHeight;
        array = new JSONArray();

        setSortByPosition(true);
        addOperator(new SetStrokingColorSpace());
        addOperator(new SetNonStrokingColorSpace());
        addOperator(new SetStrokingDeviceCMYKColor());
        addOperator(new SetNonStrokingDeviceCMYKColor());
        addOperator(new SetNonStrokingDeviceRGBColor());
        addOperator(new SetStrokingDeviceRGBColor());
        addOperator(new SetNonStrokingDeviceGrayColor());
        addOperator(new SetStrokingDeviceGrayColor());
        addOperator(new SetStrokingColor());
        addOperator(new SetStrokingColorN());
        addOperator(new SetNonStrokingColor());
        addOperator(new SetNonStrokingColorN());
    }

    @Override
    protected void processTextPosition(TextPosition textPosition) {
        Matrix prevMatrix;
        Matrix matrix = textPosition.getTextMatrix();
        if (prevTextPosition == null || (prevMatrix = prevTextPosition.getTextMatrix()).getTranslateY() != matrix.getTranslateY()
                || prevMatrix.getTranslateX() + prevMatrix.getScalingFactorX() < matrix.getTranslateX() - 0.1D * textPosition.getFontSizeInPt()) {
            addLine();
            words = new JSONArray();
            word = null;
            width = 0.0D;

            anchor = new JSONObject();
            int height = pdfHelper.pointToPixel(matrix.getScalingFactorY());
            anchor.put("height", height);
            anchor.put("x", pdfHelper.pointToPixel(matrix.getTranslateX()));
            anchor.put("y", pageHeight - height - pdfHelper.pointToPixel(matrix.getTranslateY()));
        }

        addWord(textPosition);
        width += matrix.getScalingFactorX();
        prevTextPosition = textPosition;
    }

    private void addLine() {
        if (prevTextPosition == null || word == null || word.isEmpty())
            return;

        words.add(word);
        JSONObject paragraph = new JSONObject();
        merge(paragraph, words);
        paragraph.put("words", words);
        JSONArray paragraphs = new JSONArray();
        paragraphs.add(paragraph);
        JSONObject text = new JSONObject();
        merge(text, paragraphs);
        text.put("paragraphs", paragraphs);
        JSONObject object = new JSONObject();
        object.put("text", text);

        anchor.put("width", pdfHelper.pointToPixel(width));
        object.put("anchor", anchor);
        array.add(object);
    }

    private void addWord(TextPosition textPosition) {
        JSONObject color = pdfHelper.toColor(getGraphicsState().getNonStrokingColor().getComponents());
        if (word == null)
            newWord(textPosition, color);
        else if (notSameStyle(textPosition, color)) {
            words.add(word);
            newWord(textPosition, color);
        }
        word.put("word", word.containsKey("word") ? (word.getString("word") + textPosition.getUnicode()) : textPosition.getUnicode());
    }

    private boolean notSameStyle(TextPosition textPosition, JSONObject color) {
        return word.getFloatValue("fontSize") != textPosition.getFontSizeInPt() || !word.getJSONObject("color").equals(color);
    }

    private void newWord(TextPosition textPosition, JSONObject color) {
        word = new JSONObject();
        word.put("fontSize", textPosition.getFontSizeInPt());
        word.put("color", color);
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

    public JSONArray getArray() {
        addLine();

        return array;
    }
}
