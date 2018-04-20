package org.lpw.tephra.office.pptx;

import com.alibaba.fastjson.JSONObject;

/**
 * 解析器。
 *
 * @author lpw
 */
public interface Parser {
    /**
     * 获取解析器名称。
     *
     * @return 解析器名称。
     */
    String getName();

    /**
     * 解析。
     *
     * @param path 文件路径。
     * @param rels 资源。
     * @return 数据。
     */
    JSONObject parse(String path, JSONObject rels);
}
