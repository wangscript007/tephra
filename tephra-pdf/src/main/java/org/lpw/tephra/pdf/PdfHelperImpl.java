package org.lpw.tephra.pdf;

import org.lpw.tephra.util.Numeric;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * @author lpw
 */
@Component("tephra.pdf.helper")
public class PdfHelperImpl implements PdfHelper {
    @Inject
    private Numeric numeric;

    @Override
    public int pointToPixel(double point) {
        return numeric.toInt(point * 96 / 72);
    }
}
