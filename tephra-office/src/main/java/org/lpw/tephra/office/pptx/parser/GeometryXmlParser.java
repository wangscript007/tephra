package org.lpw.tephra.office.pptx.parser;

import com.alibaba.fastjson.JSONObject;

/**
 * XML数据解析器。
 *
 * @author lpw
 */
public interface GeometryXmlParser {
    void putFillRect(String xml, JSONObject texture);
}
