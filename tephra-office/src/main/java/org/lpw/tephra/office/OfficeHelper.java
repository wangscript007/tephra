package org.lpw.tephra.office;

import com.alibaba.fastjson.JSONObject;

import java.awt.Color;

/**
 * @author lpw
 */
public interface OfficeHelper {
    /**
     * 是否为PPT文件。
     *
     * @param contentType 文件类型。
     * @param fileName    文件名。
     * @return 如果是则返回true；否则返回false。
     */
    boolean isPpt(String contentType, String fileName);

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
    long pixelToEmu(int pixel);

    /**
     * EMU值转化为像素值。
     *
     * @param emu EMU值。
     * @return 像素值。
     */
    int emuToPixel(long emu);

    /**
     * EMU值转化为磅值。
     *
     * @param emu EMU值。
     * @return 磅值。
     */
    double emuToPoint(long emu);

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

    /**
     * 获取百分比数值。
     *
     * @param percent 百分比。
     * @return 数值。
     */
    double fromPercent(int percent);

    /**
     * 转化为百分比值。
     *
     * @param value 数值。
     * @return 百分比值。
     */
    int toPercent(double value);

    /**
     * 颜色值转化为JSON数据。
     *
     * @param color 颜色值。
     * @return JSON数据。
     */
    JSONObject colorToJson(Color color);

    /**
     * JSON数据转化为颜色值。
     *
     * @param object JSON数据。
     * @return 颜色值。
     */
    Color jsonToColor(JSONObject object);
}
