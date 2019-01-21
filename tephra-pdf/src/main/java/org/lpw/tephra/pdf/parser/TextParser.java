package org.lpw.tephra.pdf.parser;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.apache.pdfbox.util.Matrix;
import org.lpw.tephra.pdf.PdfHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lpw
 */
public class TextParser extends PDFTextStripper {
    private PdfHelper pdfHelper;
    private int pageHeight;
    private List<List<TextPosition>> positions;
    private Matrix prevMatrix;
    private JSONArray array;
    private String[] merges = {"horizontalAlign", "fontFamily", "fontSize", "color", "bold", "italic", "underline", "strikethrough",
            "subscript", "superscript"};

    public TextParser(PdfHelper pdfHelper, int pageHeight) throws IOException {
        super();

        this.pdfHelper = pdfHelper;
        this.pageHeight = pageHeight;
        positions = new ArrayList<>();

        setSortByPosition(true);
    }

    @Override
    protected void processTextPosition(TextPosition textPosition) {
        Matrix matrix = textPosition.getTextMatrix();
        if (prevMatrix == null || prevMatrix.getTranslateY() != matrix.getTranslateY()
                || prevMatrix.getTranslateX() + prevMatrix.getScalingFactorX() < matrix.getTranslateX() - 0.05D)
            positions.add(new ArrayList<>());

        prevMatrix = matrix;
        positions.get(positions.size() - 1).add(textPosition);
    }

    public JSONArray getArray() {
        if (array == null)
            parse();

        return array;
    }

    private void parse() {
        array = new JSONArray();
        positions.forEach(list -> {
            JSONArray words = new JSONArray();
            JSONObject word = new JSONObject();
            double width = 0;
            StringBuilder sb = new StringBuilder();
            TextPosition prevTextPosition = null;
            for (TextPosition textPosition : list) {
                if (prevTextPosition != null && prevTextPosition.getFontSizeInPt() != textPosition.getFontSizeInPt()) {
                    addWord(words, word, sb, textPosition);
                    word = new JSONObject();
                    sb = new StringBuilder();
                }
                width += textPosition.getTextMatrix().getScalingFactorX();
                sb.append(textPosition.getUnicode());

                prevTextPosition = textPosition;
            }
            addWord(words, word, sb, prevTextPosition);

            JSONObject paragraph = new JSONObject();
            paragraph.put("words", words);
            merge(paragraph, words);
            JSONArray paragraphs = new JSONArray();
            paragraphs.add(paragraph);
            JSONObject text = new JSONObject();
            text.put("paragraphs", paragraphs);
            merge(text, paragraphs);
            JSONObject object = new JSONObject();
            object.put("text", text);

            JSONObject anchor = new JSONObject();
            Matrix matrix = list.get(0).getTextMatrix();
            anchor.put("width", pdfHelper.pointToPixel(width));
            int height = pdfHelper.pointToPixel(matrix.getScalingFactorY());
            anchor.put("height", height);
            anchor.put("x", pdfHelper.pointToPixel(matrix.getTranslateX()));
            anchor.put("y", pageHeight - height - pdfHelper.pointToPixel(matrix.getTranslateY()));
            object.put("anchor", anchor);
            array.add(object);
        });
    }

    private void addWord(JSONArray words, JSONObject word, StringBuilder sb, TextPosition textPosition) {
        word.put("word", sb.toString());
        setStyle(word, textPosition);
        words.add(word);
    }

    private void setStyle(JSONObject object, TextPosition textPosition) {
        object.put("fontSize", textPosition.getFontSizeInPt());
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
}
