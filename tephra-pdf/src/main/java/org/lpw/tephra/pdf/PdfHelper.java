package org.lpw.tephra.pdf;

/**
 * @author lpw
 */
public interface PdfHelper {
    /**
     * 磅值转化为像素值。
     *
     * @param point 磅值。
     * @return 像素值。
     */
    int pointToPixel(double point);
}
