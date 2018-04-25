package org.lpw.tephra.office;

/**
 * @author lpw
 */
public interface OfficeHelper {
    /**
     * 获取临时目录。
     *
     * @param name 名称。
     * @return 临时目录。
     */
    String getTempPath(String name);

    /**
     * 像素值转化为EMU值。
     *
     * @param pixel 像素值。
     * @return EMU值。
     */
    int pixelToEmu(int pixel);

    /**
     * EMU值转化为像素值。
     *
     * @param emu EMU值。
     * @return 像素值。
     */
    int emuToPixel(int emu);

    /**
     * 磅值转化为像素值。
     *
     * @param point 磅值。
     * @return 像素值。
     */
    int pointToPixel(double point);

    /**
     * 像素值转化为磅值。
     *
     * @param pixel 像素值。
     * @return 磅值。
     */
    double pixelToPoint(int pixel);
}
