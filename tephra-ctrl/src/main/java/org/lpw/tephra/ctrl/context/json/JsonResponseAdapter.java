package org.lpw.tephra.ctrl.context.json;

import org.lpw.tephra.ctrl.context.ResponseAdapter;
import org.lpw.tephra.ctrl.context.ResponseSender;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author lpw
 */
public class JsonResponseAdapter implements ResponseAdapter {
    private ResponseSender responseSender;
    private String sessionId;
    private ByteArrayOutputStream outputStream;

    public JsonResponseAdapter(ResponseSender responseSender, String sessionId) {
        this.responseSender = responseSender;
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
        responseSender.send(sessionId, outputStream);
    }

    @Override
    public void redirectTo(String url) {
    }

    @Override
    public void sendError(int code) {
    }
}
