package org.lpw.tephra.ctrl.http.context;

import com.alibaba.fastjson.JSONObject;
import org.lpw.tephra.bean.BeanFactory;
import org.lpw.tephra.ctrl.context.RequestAdapter;
import org.lpw.tephra.util.Converter;
import org.lpw.tephra.util.Io;
import org.lpw.tephra.util.Json;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Validator;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lpw
 */
public class RequestAdapterImpl implements RequestAdapter {
    private HttpServletRequest request;
    private String url;
    private String uri;
    private Map<String, String> map;
    private String content;
    private Validator validator;
    private Converter converter;
    private Logger logger;

    public RequestAdapterImpl(HttpServletRequest request, String uri) {
        this.request = request;
        this.uri = uri;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public String get(String name) {
        return getMap().get(name);
    }

    @Override
    public String[] getAsArray(String name) {
        return request.getParameterValues(name);
    }

    @Override
    public Map<String, String> getMap() {
        if (map == null) {
            if (content == null && getFromInputStream().length() == 0)
                map = new HashMap<>();
            request.getParameterMap().forEach((key, value) -> map.put(key, getConverter().toString(value)));
        }

        return map;
    }

    @Override
    public String getFromInputStream() {
        if (content != null)
            return content;

        String contentType = request.getHeader("content-type");
        if (contentType == null)
            contentType = "";
        boolean boundary = contentType.startsWith("multipart/form-data; boundary=");
        if (!boundary && contentType.startsWith("multipart/form-data"))
            return content = "";

        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            BeanFactory.getBean(Io.class).copy(request.getInputStream(), output);
            content = output.toString();
            if (getLogger().isDebugEnable())
                getLogger().debug("[{}]获取InputStream中的数据[{}:{}]。", uri, contentType, content);

            if (boundary)
                fromBoundary(contentType);
            else if (!getValidator().isEmpty(content)) {
                char ch = content.charAt(0);
                if (ch == '{')
                    fromJson(true);
                else if (ch == '[')
                    fromJson(false);
                else
                    map = getConverter().toParameterMap(content);
            }

            return content;
        } catch (IOException e) {
            getLogger().warn(e, "[{}]获取InputStream中的数据[{}]时发生异常！", uri, contentType);

            return "";
        }
    }

    private void fromBoundary(String contentType) {
        if (map == null)
            map = new HashMap<>();
        String boundary = "--" + contentType.substring(contentType.indexOf('=') + 1);
        for (String string : content.split(boundary)) {
            string = string.trim();
            if (string.equals(""))
                continue;

            int indexOf = string.indexOf('"');
            if (indexOf == -1)
                continue;

            string = string.substring(indexOf + 1);
            if ((indexOf = string.indexOf('"')) == -1)
                continue;

            map.put(string.substring(0, indexOf), string.substring(indexOf + 1).trim());
        }
        if (getLogger().isDebugEnable())
            getLogger().debug("[{}]获取Boundary参数[{}]。", uri, map);
    }

    private void fromJson(boolean object) {
        try {
            map = new HashMap<>();
            if (!object)
                return;

            JSONObject obj = BeanFactory.getBean(Json.class).toObject(content);
            if (obj != null)
                obj.forEach((key, value) -> map.put(key, value.toString()));
        } catch (Throwable throwable) {
            getLogger().warn(throwable, "[{}]从JSON内容[{}]中获取参数集异常！", uri, content);
        }
    }

    private Validator getValidator() {
        if (validator == null)
            validator = BeanFactory.getBean(Validator.class);

        return validator;
    }

    private Converter getConverter() {
        if (converter == null)
            converter = BeanFactory.getBean(Converter.class);

        return converter;
    }

    private Logger getLogger() {
        if (logger == null)
            logger = BeanFactory.getBean(Logger.class);

        return logger;
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
