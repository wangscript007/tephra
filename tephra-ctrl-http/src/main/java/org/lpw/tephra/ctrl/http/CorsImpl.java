package org.lpw.tephra.ctrl.http;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.tephra.storage.StorageListener;
import org.lpw.tephra.storage.Storages;
import org.lpw.tephra.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.Set;

@Controller("tephra.ctrl.http.cors")
public class CorsImpl implements Cors, StorageListener {
    @Inject
    private Validator validator;
    @Inject
    private Json json;
    @Inject
    private Io io;
    @Inject
    private Converter converter;
    @Inject
    private Logger logger;
    @Value("${tephra.ctrl.http.cors:/WEB-INF/http/cors.json}")
    private String cors;
    private Set<String> corsOrigins;
    private String corsMethods;
    private String corsHeaders;

    @Override
    public boolean is(HttpServletRequest request, HttpServletResponse response) {
        if (request.getMethod().equals("OPTIONS")) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);

            return true;
        }

        return false;
    }

    @Override
    public void set(HttpServletRequest request, HttpServletResponse response) {
        if (!validator.isEmpty(corsOrigins)) {
            String origin = request.getHeader("Origin");
            if (corsOrigins.contains("*") || corsOrigins.contains(origin)) {
                response.addHeader("Access-Control-Allow-Credentials", "true");
                response.addHeader("Access-Control-Allow-Origin", "*");
                response.addHeader("Access-Control-Allow-Methods", corsMethods);
                response.addHeader("Access-Control-Allow-Headers", corsHeaders);
            }
        }
    }

    @Override
    public String getStorageType() {
        return Storages.TYPE_DISK;
    }

    @Override
    public String[] getScanPathes() {
        return new String[]{cors};
    }

    @Override
    public void onStorageChanged(String path, String absolutePath) {
        JSONObject object = json.toObject(io.readAsString(absolutePath));
        if (object == null) {
            corsOrigins = new HashSet<>();
            corsMethods = "";
            corsHeaders = "";
        } else {
            corsOrigins = new HashSet<>();
            if (object.containsKey("origin")) {
                JSONArray array = object.getJSONArray("origin");
                for (int i = 0, size = array.size(); i < size; i++)
                    corsOrigins.add(array.getString(i));
            }
            corsMethods = toString(object.getJSONArray("methods")).toUpperCase();
            corsHeaders = toString(object.getJSONArray("headers"));
        }
        if (logger.isInfoEnable())
            logger.info("设置跨域[{}:{}:{}]。", converter.toString(corsOrigins), corsMethods, corsHeaders);
    }

    private String toString(JSONArray array) {
        if (array == null)
            return "";

        Set<String> set = new HashSet<>();
        for (int i = 0, size = array.size(); i < size; i++)
            set.add(array.getString(i));

        StringBuilder sb = new StringBuilder();
        for (String string : set)
            sb.append(',').append(string);

        return sb.length() == 0 ? "" : sb.substring(1);
    }
}
