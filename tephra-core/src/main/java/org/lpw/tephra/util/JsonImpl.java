package org.lpw.tephra.util;

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

    @Override
    public void add(JSONObject json, String key, Object object) {
        if (validator.isEmpty(key))
            return;

        if (!json.containsKey(key)) {
            json.put(key, object);

            return;
        }

        JSONArray array = toArray(json.get(key));
        array.add(object);
        json.put(key, array);
    }

    @Override
    public void addAsArray(JSONObject json, String key, Object object) {
        if (validator.isEmpty(key))
            return;

        Object obj = json.get(key);
        JSONArray array = obj == null ? new JSONArray() : toArray(obj);
        array.add(object);
        json.put(key, array);
    }

    private JSONArray toArray(Object object) {
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
}
