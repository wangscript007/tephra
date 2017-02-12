package org.lpw.tephra.util;

import com.alibaba.fastjson.JSONObject;

/**
 * @author lpw
 */
public interface Json {
    /**
     * 添加数据。如果key已存在则自动添加为数组。
     *
     * @param json   JSON对象。
     * @param key    key值。
     * @param object 数据。
     */
    void add(JSONObject json, String key, Object object);

    /**
     * 添加为数组。
     *
     * @param json   JSON对象。
     * @param key    属性key。
     * @param object 要添加的对象。
     */
    void addAsArray(JSONObject json, String key, Object object);

    /**
     * 获取字符串数组。
     *
     * @param json JSON对象。
     * @param key  属性key。
     * @return 字符串数组。
     */
    String[] getAsStringArray(JSONObject json, String key);

    /**
     * 将XML字符串转化为JSON对象。
     *
     * @param xml XML字符串。
     * @return JSON对象；如果转化失败则返回null。
     */
    JSONObject fromXml(String xml);
}
