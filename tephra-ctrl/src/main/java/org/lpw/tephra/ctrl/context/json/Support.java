package org.lpw.tephra.ctrl.context.json;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lpw
 */
public class Support {
    private JSONObject object;
    private Map<String, String> map;

    Support(JSONObject object) {
        this.object = object == null ? new JSONObject() : object;
    }

    public String get(String name) {
        return object.getString(name);
    }

    public Map<String, String> getMap() {
        if (map == null) {
            map = new HashMap<>();
            for (String key : object.keySet())
                map.put(key, object.getString(key));
        }

        return map;
    }
}
