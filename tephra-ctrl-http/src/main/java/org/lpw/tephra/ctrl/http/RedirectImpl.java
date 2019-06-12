package org.lpw.tephra.ctrl.http;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.tephra.storage.StorageListener;
import org.lpw.tephra.storage.Storages;
import org.lpw.tephra.util.Io;
import org.lpw.tephra.util.Json;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * @author lpw
 */
@Controller("tephra.ctrl.http.redirect")
public class RedirectImpl implements Redirect, StorageListener {
    @Inject
    private Validator validator;
    @Inject
    private Io io;
    @Inject
    private Json json;
    @Inject
    private Logger logger;
    @Value("${tephra.ctrl.http.redirect:/WEB-INF/http/redirect.json}")
    private String redirect;
    private String uri;
    private Set<String> hosts;
    private Set<String> regexes;

    @Override
    public boolean redirect(HttpServletRequest request, String uri, HttpServletResponse response) throws IOException {
        if (!uri.equals(this.uri))
            return false;

        String to = request.getParameter("to");
        if (to != null && enable(to))
            response.sendRedirect(getUrl(to, request));
        else {
            response.sendError(404);
            logger.warn(null, "未配置转发目标[{}]。", to);
        }

        return true;
    }

    private boolean enable(String to) {
        int indexOf = to.indexOf("://");
        if (indexOf == -1) {
            logger.warn(null, "转发地址[{}]格式解析失败！", to);

            return false;
        }

        String host = to.substring(indexOf + 3);
        if ((indexOf = host.indexOf('/')) == -1) {
            logger.warn(null, "转发地址[{}]格式解析失败！", to);

            return false;
        }

        host = host.substring(0, indexOf);
        if ((indexOf = host.indexOf(':')) > -1)
            host = host.substring(0, indexOf);
        if (logger.isDebugEnable())
            logger.debug("获取转发域名[{}]。", host);
        if (hosts.contains(host))
            return true;

        for (String regex : regexes)
            if (validator.isMatchRegex(regex, host))
                return true;

        return false;
    }

    private String getUrl(String url, HttpServletRequest request) {
        String anchor = "";
        int indexOf = url.indexOf('#');
        if (indexOf > -1) {
            anchor = url.substring(indexOf);
            url = url.substring(0, indexOf);
        }

        StringBuilder sb = new StringBuilder(url).append(url.indexOf('?') == -1 ? '?' : '&');
        boolean has = false;
        for (Enumeration<String> names = request.getParameterNames(); names.hasMoreElements(); ) {
            String name = names.nextElement();
            if (name.equals("to"))
                continue;

            if (has)
                sb.append('&');
            sb.append(name).append('=').append(request.getParameter(name));
            has = true;
        }
        sb.append(anchor);
        int last = sb.length() - 1;
        if (sb.charAt(last) == '?')
            sb.deleteCharAt(last);

        if (logger.isDebugEnable())
            logger.debug("转发请求[{}]。", sb);

        return sb.toString();
    }

    @Override
    public String getStorageType() {
        return Storages.TYPE_DISK;
    }

    @Override
    public String[] getScanPathes() {
        return new String[]{redirect};
    }

    @Override
    public void onStorageChanged(String path, String absolutePath) {
        String string = io.readAsString(absolutePath);
        JSONObject object = json.toObject(string);
        if (object == null) {
            logger.warn(null, "读取转发配置[{}:{}]失败！", path, string);

            return;
        }

        uri = object.containsKey("uri") ? object.getString("uri") : "/tephra/ctrl-http/redirect";
        hosts = toSet(object, "hosts");
        regexes = toSet(object, "regexes");
        if (logger.isInfoEnable())
            logger.info("更新转发配置[{}:{}:{}]。", uri, hosts.toString(), regexes.toString());
    }

    private Set<String> toSet(JSONObject object, String key) {
        Set<String> set = new HashSet<>();
        if (!object.containsKey(key))
            return set;

        JSONArray array = object.getJSONArray(key);
        for (int i = 0, size = array.size(); i < size; i++)
            set.add(array.getString(i).trim());
        set.remove("");

        return set;
    }
}
