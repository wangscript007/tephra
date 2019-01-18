package org.lpw.tephra.pdf.parser;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.apache.pdfbox.util.Matrix;
import org.lpw.tephra.pdf.PdfHelper;

import java.io.IOException;

/**
 * @author lpw
 */
public class TextParser extends PDFTextStripper {
    private PdfHelper pdfHelper;
    private int pageHeight;
    private JSONArray array;

    public TextParser(PdfHelper pdfHelper,int pageHeight) throws IOException {
        super();

        this.pdfHelper = pdfHelper;
        this.pageHeight=pageHeight;
        array = new JSONArray();

        setSortByPosition(true);
    }

    @Override
    protected void processTextPosition(TextPosition textPosition) {
        JSONObject object = new JSONObject();
        JSONObject anchor = new JSONObject();
        Matrix matrix = textPosition.getTextMatrix();
        anchor.put("width", pdfHelper.pointToPixel(matrix.getScalingFactorX()));
        int height = pdfHelper.pointToPixel(matrix.getScalingFactorY());
        anchor.put("height", height);
        anchor.put("x", pdfHelper.pointToPixel(matrix.getTranslateX()));
        anchor.put("y", pageHeight - height - pdfHelper.pointToPixel(matrix.getTranslateY()));
        object.put("anchor", anchor);


        JSONArray words = new JSONArray();
        JSONObject word = new JSONObject();
        word.put("word", textPosition.getUnicode());
        words.add(word);

        JSONObject paragraph = new JSONObject();
        paragraph.put("words", words);
        JSONArray paragraphs = new JSONArray();
        paragraphs.add(paragraph);
        JSONObject text = new JSONObject();
        text.put("fontSize", pdfHelper.pointToPixel(textPosition.getFontSizeInPt()));
        text.put("paragraphs", paragraphs);
        object.put("text", text);
        array.add(object);
    }

    public JSONArray getArray() {
        return array;
    }
}
