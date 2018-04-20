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
     * 字体值转化为像素值。
     *
     * @param fontSize 字体值。
     * @return 像素值。
     */
    int fontSizeToPixel(int fontSize);

    /**
     * 像素值转化为字体值。
     *
     * @param pixel 像素值。
     * @return 字体值。
     */
    int pixelToFontSize(int pixel);
}
