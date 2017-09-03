package org.lpw.tephra.ctrl.socket.context;

import org.lpw.tephra.ctrl.context.ResponseAdapter;
import org.lpw.tephra.ctrl.socket.SocketHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author lpw
 */
public class ResponseAdapterImpl implements ResponseAdapter {
    private SocketHelper socketHelper;
    private String sessionId;
    private ByteArrayOutputStream outputStream;

    public ResponseAdapterImpl(SocketHelper socketHelper, String sessionId) {
        this.socketHelper = socketHelper;
        this.sessionId = sessionId;
        outputStream = new ByteArrayOutputStream();
    }

    @Override
    public void setContentType(String contentType) {
    }

    @Override
    public void setHeader(String name, String value) {
    }

    @Override
    public OutputStream getOutputStream() {
        return outputStream;
    }

    @Override
    public void send() throws IOException {
        outputStream.close();
        socketHelper.send(sessionId, outputStream.toByteArray());
    }

    @Override
    public void redirectTo(String url) {
    }

    @Override
    public void sendError(int code) {
    }
}
