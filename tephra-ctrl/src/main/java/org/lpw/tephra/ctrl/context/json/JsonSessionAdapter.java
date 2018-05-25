package org.lpw.tephra.ctrl.context.json;

import org.lpw.tephra.ctrl.context.SessionAdapter;

/**
 * @author lpw
 */
public class JsonSessionAdapter implements SessionAdapter {
    private String sessionId;

    public JsonSessionAdapter(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String getId() {
        return sessionId;
    }
}
