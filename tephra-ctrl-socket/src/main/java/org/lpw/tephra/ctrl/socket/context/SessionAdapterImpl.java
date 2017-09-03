package org.lpw.tephra.ctrl.socket.context;

import org.lpw.tephra.ctrl.context.SessionAdapter;

/**
 * @author lpw
 */
public class SessionAdapterImpl implements SessionAdapter {
    protected String sessionId;

    public SessionAdapterImpl(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String getId() {
        return sessionId;
    }
}
