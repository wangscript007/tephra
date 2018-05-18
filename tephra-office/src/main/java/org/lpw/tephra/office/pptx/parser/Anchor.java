package org.lpw.tephra.office.pptx.parser;

import com.alibaba.fastjson.JSONObject;

import java.awt.geom.Rectangle2D;

/**
 * 位置解析器。
 *
 * @author lpw
 */
public interface Anchor {
    /**
     * 解析。
     *
     * @param rectangle2D 位置。
     * @param shape       解析数据。
     */
    void parse(Rectangle2D rectangle2D, JSONObject shape);
}
