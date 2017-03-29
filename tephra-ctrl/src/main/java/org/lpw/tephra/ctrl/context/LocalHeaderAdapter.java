package org.lpw.tephra.ctrl.context;

import java.util.HashMap;
import java.util.Map;

/**
 * 基于Map的头信息适配器实现。
 *
 * @author lpw
 */
public class LocalHeaderAdapter implements HeaderAdapter {
    protected String ip;
    protected Map<String, String> map;

    public LocalHeaderAdapter(String ip, Map<String, String> map) {
        this.ip = ip == null ? "127.0.0.1" : ip;
        this.map = map == null ? new HashMap<>() : map;
    }

    @Override
    public String get(String name) {
        return map.get(name);
    }

    @Override
    public String getIp() {
        return ip;
    }

    @Override
    public Map<String, String> getMap() {
        return map;
    }
}
