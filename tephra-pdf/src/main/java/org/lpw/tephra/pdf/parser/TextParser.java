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
import org.lpw.tephra.pdf.PdfHelper;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author lpw
 */
public class TextParser extends PDFTextStripper {
    private PdfHelper pdfHelper;
    private int pageHeight;
    private JSONArray array;
    private JSONObject text;
    private TextPosition prevTextPosition;
    private JSONObject anchor;
    private List<String> fontFamilies = Arrays.asList("Arial", "TimesNewRoman", "SimSun", "YaHei", "SansSerif");

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

        if (prevTextPosition == null || prevTextPosition.getEndY() != textPosition.getEndY()
                || prevTextPosition.getFontSizeInPt() != textPosition.getFontSizeInPt()
                || prevTextPosition.getEndX() + prevTextPosition.getWidth() < textPosition.getX())
            addLine();
        addWord(textPosition);
        prevTextPosition = textPosition;
    }

    private void addLine() {
        if (prevTextPosition == null || text == null || text.isEmpty())
            return;

        anchor.put("width", pdfHelper.pointToPixel(prevTextPosition.getEndX()) - anchor.getIntValue("x"));
        text.put("anchor", anchor);
        array.add(text);
        text = null;
    }

    private void addWord(TextPosition textPosition) {
        JSONObject color = pdfHelper.toJsonColor(getGraphicsState().getNonStrokingColor().getComponents());
        if (text == null)
            newWord(textPosition, color);
        else if (notSameStyle(textPosition, color)) {
            addLine();
            newWord(textPosition, color);
        }
        text.put("text", text.containsKey("text") ? (text.getString("text") + textPosition.getUnicode()) : textPosition.getUnicode());
    }

    private boolean notSameStyle(TextPosition textPosition, JSONObject color) {
        return !textPosition.getFont().getName().equals(text.getString("fontFamily"))
                || text.getFloatValue("fontSize") != textPosition.getFontSizeInPt()
                || !text.getJSONObject("color").equals(color);
    }

    private void newWord(TextPosition textPosition, JSONObject color) {
        anchor = new JSONObject();
        anchor.put("x", pdfHelper.pointToPixel(textPosition.getX()));
        anchor.put("y", pageHeight - pdfHelper.pointToPixel(textPosition.getTextMatrix().getScalingFactorY() + textPosition.getEndY()));
        anchor.put("height", pdfHelper.pointToPixel(textPosition.getTextMatrix().getScalingFactorY()));

        text = new JSONObject();
        String name = textPosition.getFont().getName();
        text.put("fontFamily", getFontFamily(name));
        if (name.contains("Bold"))
            text.put("bold", true);
        text.put("fontSize", textPosition.getFontSizeInPt());
        text.put("letterSpacing", getGraphicsState().getTextState().getCharacterSpacing());
        text.put("color", color);
    }

    private String getFontFamily(String name) {
        for (String fontFamily : fontFamilies)
            if (name.contains(fontFamily))
                return fontFamily;

        return name;
    }

    public JSONArray getArray() {
        addLine();

        return array;
    }
}
