package org.lpw.tephra.pdf;

import com.alibaba.fastjson.JSONObject;

import java.awt.Color;

/**
 * @author lpw
 */
public interface PdfHelper {
    /**
     * 验证是否为PDF文件。
     *
     * @param contentType 文件类型。
     * @param fileName    文件名。
     * @return 如果是则返回true；否则返回false。
     */
    boolean is(String contentType, String fileName);

    /**
     * 磅值转化为像素值。
     *
     * @param point 磅值。
     * @return 像素值。
     */
    int pointToPixel(double point);

    /**
     * 转化为RGB颜色值。
     *
     * @param fs 颜色值数组。
     * @return 颜色值JSON对象。
     */
    JSONObject toJsonColor(float[] fs);

    /**
     * 转化为RGB颜色值。
     *
     * @param fs 颜色值数组。
     * @return 颜色值对象。
     */
    Color toColor(float[] fs);
}
