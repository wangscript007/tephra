package org.lpw.tephra.test.mock;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lpw
 */
public class MockRequestImpl implements MockRequest {
    protected String method;
    protected String uri;
    protected String serverName;
    protected int serverPort;
    protected String contextPath;
    protected Map<String, String> parameters;
    protected String content;

    @Override
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    @Override
    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    @Override
    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    @Override
    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    @Override
    public void addParameter(String name, String value) {
        if (parameters == null)
            parameters = new HashMap<>();
        parameters.put(name, value);
    }

    @Override
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String get(String name) {
        return parameters == null ? null : parameters.get(name);
    }

    @Override
    public String[] getAsArray(String name) {
        return new String[0];
    }

    @Override
    public Map<String, String> getMap() {
        return parameters;
    }

    @Override
    public String getFromInputStream() {
        return content;
    }

    @Override
    public String getServerName() {
        return serverName;
    }

    @Override
    public int getServerPort() {
        return serverPort;
    }

    @Override
    public String getContextPath() {
        return contextPath;
    }

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public String getMethod() {
        return method;
    }
}
