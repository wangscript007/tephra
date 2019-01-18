package org.lpw.tephra.pdf.parser;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.lpw.tephra.pdf.PdfHelper;

import java.io.IOException;

/**
 * @author lpw
 */
public class TextParser extends PDFTextStripper {
    private JSONArray array;
    private PdfHelper pdfHelper;

    public TextParser(PdfHelper pdfHelper) throws IOException {
        super();

        array = new JSONArray();
        this.pdfHelper = pdfHelper;

        setSortByPosition(true);
    }

    @Override
    protected void processTextPosition(TextPosition textPosition) {
        JSONObject object = new JSONObject();
        JSONObject anchor = new JSONObject();
        anchor.put("x", pdfHelper.pointToPixel(textPosition.getX()));
        anchor.put("y", pdfHelper.pointToPixel(textPosition.getY()));
        anchor.put("width", pdfHelper.pointToPixel(textPosition.getWidth()));
        anchor.put("height", pdfHelper.pointToPixel(textPosition.getHeight()));
        object.put("anchor", anchor);

        JSONArray words = new JSONArray();
        JSONObject word = new JSONObject();
        word.put("fontFamily", textPosition.getFont().getFontDescriptor().getFontFamily());
        word.put("fontSize", pdfHelper.pointToPixel(textPosition.getFontSizeInPt()));
        word.put("word", textPosition.getUnicode());
        words.add(word);

        JSONObject paragraph = new JSONObject();
        paragraph.put("words", words);
        JSONArray paragraphs = new JSONArray();
        paragraphs.add(paragraph);
        JSONObject text = new JSONObject();
        text.put("paragraphs", paragraphs);
        object.put("text", text);
        array.add(object);
    }

    public JSONArray getArray() {
        return array;
    }
}
