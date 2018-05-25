package org.lpw.tephra.ctrl.context.json;

import com.alibaba.fastjson.JSONObject;
import org.lpw.tephra.ctrl.context.HeaderAdapter;

/**
 * @author lpw
 */
public class JsonHeaderAdapter extends Support implements HeaderAdapter {
    private String ip;

    public JsonHeaderAdapter(JSONObject object, String ip) {
        super(object);
        this.ip = ip;
    }

    @Override
    public String getIp() {
        return ip;
    }
}
