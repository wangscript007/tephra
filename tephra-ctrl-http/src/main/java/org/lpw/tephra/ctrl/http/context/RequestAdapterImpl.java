package org.lpw.tephra.ctrl.http.context;

import org.lpw.tephra.bean.BeanFactory;
import org.lpw.tephra.ctrl.context.RequestAdapter;
import org.lpw.tephra.util.Converter;
import org.lpw.tephra.util.Io;
import org.lpw.tephra.util.Logger;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lpw
 */
public class RequestAdapterImpl implements RequestAdapter {
    protected HttpServletRequest request;
    protected String url;
    protected String uri;
    protected Map<String, String> map;
    protected String content;

    public RequestAdapterImpl(HttpServletRequest request, String uri) {
        this.request = request;
        this.uri = uri;
    }

    @Override
    public String get(String name) {
        String value = request.getParameter(name);
        if (value != null)
            return value;

        if (map == null)
            map = BeanFactory.getBean(Converter.class).toParameterMap(getFromInputStream());

        return map.get(name);
    }

    @Override
    public String[] getAsArray(String name) {
        return request.getParameterValues(name);
    }

    @Override
    public Map<String, String> getMap() {
        Map<String, String> map = new HashMap<>();
        Map<String, String[]> parameters = request.getParameterMap();
        parameters.forEach((key, value) -> map.put(key, value[0]));

        return map;
    }

    @Override
    public String getFromInputStream() {
        if (content != null)
            return content;

        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            BeanFactory.getBean(Io.class).copy(request.getInputStream(), output);
            output.close();
            content = output.toString();

            return content;
        } catch (IOException e) {
            BeanFactory.getBean(Logger.class).warn(e, "获取InputStream中的数据时发生异常！");

            return "";
        }
    }

    @Override
    public String getServerName() {
        return request.getServerName();
    }

    @Override
    public int getServerPort() {
        return request.getServerPort();
    }

    @Override
    public String getContextPath() {
        return request.getContextPath();
    }

    @Override
    public String getUrl() {
        if (url == null)
            url = request.getRequestURL().toString().replaceAll(uri, "");

        return url;
    }

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public String getMethod() {
        return request.getMethod();
    }
}
