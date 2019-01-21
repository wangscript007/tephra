package org.lpw.tephra.pdf;

import com.alibaba.fastjson.JSONObject;

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

    /**
     * 转化为RGB颜色值。
     *
     * @param fs 颜色值数组。
     * @return 颜色值JSON对象。
     */
    JSONObject toColor(float[] fs);
}
