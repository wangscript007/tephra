package org.lpw.tephra.ctrl.socket.context;

import com.alibaba.fastjson.JSONObject;
import org.lpw.tephra.ctrl.context.HeaderAdapter;

/**
 * @author lpw
 */
public class HeaderAdapterImpl extends JsonSupport implements HeaderAdapter {
    private String ip;

    public HeaderAdapterImpl(JSONObject object, String ip) {
        super(object);
        this.ip = ip;
    }

    @Override
    public String getIp() {
        return ip;
    }
}
