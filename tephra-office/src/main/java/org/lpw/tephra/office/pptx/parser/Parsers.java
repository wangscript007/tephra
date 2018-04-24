package org.lpw.tephra.office.pptx.parser;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.sl.usermodel.Shape;
import org.lpw.tephra.office.pptx.MediaWriter;

/**
 * @author lpw
 */
public interface Parsers {
    /**
     * 解析数据。
     *
     * @param shape       形状。
     * @param mediaWriter 媒体资源输出器。
     * @param object      解析数据。
     */
    void parse(Shape shape, MediaWriter mediaWriter, JSONObject object);
}
