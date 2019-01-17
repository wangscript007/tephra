package org.lpw.tephra.pdf.parser;

import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.IOException;

/**
 * @author lpw
 */
public class TextParser extends PDFTextStripper {
    public TextParser() throws IOException {
        super();

        setSortByPosition(true);
    }

    @Override
    protected void processTextPosition(TextPosition text) {
        System.out.println(text.getX() + ";" + text.getY() + ";" + text.getWidth() + ";" + text.getHeight() + ";"
                + text.getUnicode() + ";" + this.getCurrentPageNo());
    }
}
