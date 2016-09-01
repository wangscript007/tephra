package org.lpw.tephra.ctrl.mock;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

/**
 * @author lpw
 */
public class MockResponseImpl implements MockResponse {
    protected String contentType;
    protected OutputStream outputStream;
    protected String url;

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public String getRedirectTo() {
        return url;
    }

    @Override
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public OutputStream getOutputStream() {
        if (outputStream == null)
            outputStream = new ByteArrayOutputStream();

        return outputStream;
    }

    @Override
    public void redirectTo(String url) {
        this.url = url;
    }
}
