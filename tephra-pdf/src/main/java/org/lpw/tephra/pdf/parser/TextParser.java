package org.lpw.tephra.pdf.parser;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.pdfbox.contentstream.operator.DrawObject;
import org.apache.pdfbox.contentstream.operator.color.SetNonStrokingColor;
import org.apache.pdfbox.contentstream.operator.color.SetNonStrokingColorN;
import org.apache.pdfbox.contentstream.operator.color.SetNonStrokingColorSpace;
import org.apache.pdfbox.contentstream.operator.color.SetNonStrokingDeviceCMYKColor;
import org.apache.pdfbox.contentstream.operator.color.SetNonStrokingDeviceGrayColor;
import org.apache.pdfbox.contentstream.operator.color.SetNonStrokingDeviceRGBColor;
import org.apache.pdfbox.contentstream.operator.state.Concatenate;
import org.apache.pdfbox.contentstream.operator.state.Restore;
import org.apache.pdfbox.contentstream.operator.state.Save;
import org.apache.pdfbox.contentstream.operator.state.SetGraphicsStateParameters;
import org.apache.pdfbox.contentstream.operator.state.SetMatrix;
import org.apache.pdfbox.contentstream.operator.text.BeginText;
import org.apache.pdfbox.contentstream.operator.text.EndText;
import org.apache.pdfbox.contentstream.operator.text.MoveText;
import org.apache.pdfbox.contentstream.operator.text.MoveTextSetLeading;
import org.apache.pdfbox.contentstream.operator.text.NextLine;
import org.apache.pdfbox.contentstream.operator.text.SetCharSpacing;
import org.apache.pdfbox.contentstream.operator.text.SetFontAndSize;
import org.apache.pdfbox.contentstream.operator.text.SetTextHorizontalScaling;
import org.apache.pdfbox.contentstream.operator.text.SetTextLeading;
import org.apache.pdfbox.contentstream.operator.text.SetTextRenderingMode;
import org.apache.pdfbox.contentstream.operator.text.SetTextRise;
import org.apache.pdfbox.contentstream.operator.text.SetWordSpacing;
import org.apache.pdfbox.contentstream.operator.text.ShowText;
import org.apache.pdfbox.contentstream.operator.text.ShowTextAdjusted;
import org.apache.pdfbox.contentstream.operator.text.ShowTextLine;
import org.apache.pdfbox.contentstream.operator.text.ShowTextLineAndSpace;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.apache.pdfbox.util.Matrix;
import org.lpw.tephra.pdf.PdfHelper;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
    private TextPosition prevTextPosition;
    private JSONObject anchor;
    private String[] merges = {"horizontalAlign", "fontFamily", "fontSize", "color", "bold", "italic", "underline", "strikethrough",
            "subscript", "superscript"};
    private List<String> fontFamilies = Arrays.asList("Arial", "TimesNewRoman", "SimSun", "YaHei", "SansSerif");
    private double height;

    public TextParser(PdfHelper pdfHelper, int pageHeight) throws IOException {
        super();

        this.pdfHelper = pdfHelper;
        this.pageHeight = pageHeight;
        array = new JSONArray();

        setSortByPosition(true);
        setSuppressDuplicateOverlappingText(true);

        addOperator(new BeginText());
        addOperator(new Concatenate());
        addOperator(new DrawObject());
        addOperator(new EndText());
        addOperator(new MoveText());
        addOperator(new MoveTextSetLeading());
        addOperator(new NextLine());
        addOperator(new Restore());
        addOperator(new Save());
        addOperator(new SetCharSpacing());
        addOperator(new SetFontAndSize());
        addOperator(new SetGraphicsStateParameters());
        addOperator(new SetMatrix());
        addOperator(new SetNonStrokingColor());
        addOperator(new SetNonStrokingColorN());
        addOperator(new SetNonStrokingColorSpace());
        addOperator(new SetNonStrokingDeviceCMYKColor());
        addOperator(new SetNonStrokingDeviceGrayColor());
        addOperator(new SetNonStrokingDeviceRGBColor());
        addOperator(new SetTextHorizontalScaling());
        addOperator(new SetTextLeading());
        addOperator(new SetTextRenderingMode());
        addOperator(new SetTextRise());
        addOperator(new SetWordSpacing());
        addOperator(new ShowText());
        addOperator(new ShowTextAdjusted());
        addOperator(new ShowTextLine());
        addOperator(new ShowTextLineAndSpace());
    }

    @Override
    protected void processTextPosition(TextPosition textPosition) {
        super.processTextPosition(textPosition);

        Matrix matrix = textPosition.getTextMatrix();
        if (prevTextPosition == null || prevTextPosition.getEndY() != textPosition.getEndY()
                || prevTextPosition.getEndX() < textPosition.getX() - matrix.getScalingFactorX() / 10) {
            addLine();
            words = new JSONArray();
            word = null;
            height = 0.0D;

            anchor = new JSONObject();
            anchor.put("x", pdfHelper.pointToPixel(textPosition.getX()));
        }
        height = Math.max(height, matrix.getScalingFactorY());
        addWord(textPosition);
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

        int height = pdfHelper.pointToPixel(this.height);
        anchor.put("y", pageHeight - height - pdfHelper.pointToPixel(prevTextPosition.getEndY()));
        anchor.put("width", pdfHelper.pointToPixel(prevTextPosition.getEndX()) - anchor.getIntValue("x"));
        anchor.put("height", height);
        object.put("anchor", anchor);
        array.add(object);
    }

    private void addWord(TextPosition textPosition) {
        JSONObject color = pdfHelper.toJsonColor(getGraphicsState().getNonStrokingColor().getComponents());
        if (word == null)
            newWord(textPosition, color);
        else if (notSameStyle(textPosition, color)) {
            words.add(word);
            newWord(textPosition, color);
        }
        word.put("word", word.containsKey("word") ? (word.getString("word") + textPosition.getUnicode()) : textPosition.getUnicode());
    }

    private boolean notSameStyle(TextPosition textPosition, JSONObject color) {
        return !textPosition.getFont().getName().equals(word.getString("fontFamily"))
                || word.getFloatValue("fontSize") != textPosition.getFontSizeInPt()
                || !word.getJSONObject("color").equals(color);
    }

    private void newWord(TextPosition textPosition, JSONObject color) {
        word = new JSONObject();
        String name = textPosition.getFont().getName();
        word.put("fontFamily", getFontFamily(name));
        if (name.contains("Bold"))
            word.put("bold", true);
        word.put("fontSize", textPosition.getFontSizeInPt());
        word.put("color", color);
    }

    private String getFontFamily(String name) {
        for (String fontFamily : fontFamilies)
            if (name.contains(fontFamily))
                return fontFamily;

        return name;
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
