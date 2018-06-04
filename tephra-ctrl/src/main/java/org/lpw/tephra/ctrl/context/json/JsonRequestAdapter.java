package org.lpw.tephra.ctrl.context.json;

import com.alibaba.fastjson.JSONObject;
import org.lpw.tephra.ctrl.context.RequestAdapter;

/**
 * @author lpw
 */
public class JsonRequestAdapter extends Support implements RequestAdapter {
    private int port;
    private String id;
    private String uri;

    public JsonRequestAdapter(int port, String id, String uri, JSONObject object) {
        super(object);
        this.port = port;
        this.id = id;
        this.uri = uri;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String[] getAsArray(String name) {
        return null;
    }

    @Override
    public String getFromInputStream() {
        return null;
    }

    @Override
    public String getServerName() {
        return null;
    }

    @Override
    public int getServerPort() {
        return port;
    }

    @Override
    public String getContextPath() {
        return null;
    }

    @Override
    public String getUrl() {
        return null;
    }

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public String getMethod() {
        return null;
    }
}
