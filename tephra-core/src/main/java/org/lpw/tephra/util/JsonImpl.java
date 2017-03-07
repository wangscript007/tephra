package org.lpw.tephra.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * @author lpw
 */
@Component("tephra.util.json")
public class JsonImpl implements Json {
    @Inject
    private Validator validator;
    @Inject
    private Xml xml;
    @Inject
    private Logger logger;

    @Override
    public void add(JSONObject json, String key, Object object) {
        if (validator.isEmpty(key))
            return;

        if (!json.containsKey(key)) {
            json.put(key, object);

            return;
        }

        JSONArray array = asArray(json.get(key));
        array.add(object);
        json.put(key, array);
    }

    @Override
    public void addAsArray(JSONObject json, String key, Object object) {
        if (validator.isEmpty(key))
            return;

        Object obj = json.get(key);
        JSONArray array = obj == null ? new JSONArray() : asArray(obj);
        array.add(object);
        json.put(key, array);
    }

    private JSONArray asArray(Object object) {
        if (object instanceof JSONArray)
            return (JSONArray) object;

        JSONArray array = new JSONArray();
        array.add(object);

        return array;
    }

    @Override
    public String[] getAsStringArray(JSONObject json, String key) {
        if (json == null || validator.isEmpty(key) || !json.containsKey(key))
            return new String[0];

        JSONArray array = json.getJSONArray(key);
        String[] strings = new String[array.size()];
        for (int i = 0; i < strings.length; i++)
            strings[i] = array.getString(i);

        return strings;
    }

    @Override
    public JSONObject fromXml(String xml) {
        return this.xml.toJson(xml);
    }

    @Override
    public JSONObject toObject(Object object) {
        if (object == null)
            return null;

        if (object instanceof JSONObject)
            return (JSONObject) object;

        try {
            return JSON.parseObject(object.toString());
        } catch (Throwable throwable) {
            logger.warn(throwable, "转化对象[{}]为JSON对象时发生异常！", object);

            return null;
        }
    }

    @Override
    public JSONArray toArray(Object object) {
        if (object == null)
            return null;

        if (object instanceof JSONArray)
            return (JSONArray) object;

        try {
            return JSON.parseArray(object.toString());
        } catch (Throwable throwable) {
            logger.warn(throwable, "转化对象[{}]为JSON数组时发生异常！", object);

            return null;
        }
    }
}
