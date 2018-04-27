package org.lpw.tephra.office;

import com.alibaba.fastjson.JSONObject;

import java.awt.Color;

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

    /**
     * 转化百分比数值。
     *
     * @param max     最大值。
     * @param percent 百分比。
     * @return 数值。
     */
    int fromPercent(int max, int percent);

    /**
     * 转化为百分比数值。
     *
     * @param max   最大值。
     * @param value 数值。
     * @return 百分比数值。
     */
    int toPercent(int max, int value);

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
