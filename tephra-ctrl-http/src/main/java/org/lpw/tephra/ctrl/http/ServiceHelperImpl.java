package org.lpw.tephra.ctrl.http;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.tephra.bean.BeanFactory;
import org.lpw.tephra.ctrl.Dispatcher;
import org.lpw.tephra.ctrl.Handler;
import org.lpw.tephra.ctrl.context.HeaderAware;
import org.lpw.tephra.ctrl.context.RequestAware;
import org.lpw.tephra.ctrl.context.ResponseAware;
import org.lpw.tephra.ctrl.context.SessionAware;
import org.lpw.tephra.ctrl.http.context.CookieAware;
import org.lpw.tephra.ctrl.http.context.HeaderAdapterImpl;
import org.lpw.tephra.ctrl.http.context.RequestAdapterImpl;
import org.lpw.tephra.ctrl.http.context.ResponseAdapterImpl;
import org.lpw.tephra.ctrl.http.context.SessionAdapterImpl;
import org.lpw.tephra.ctrl.http.ws.WsHelper;
import org.lpw.tephra.ctrl.status.Status;
import org.lpw.tephra.ctrl.upload.UploadService;
import org.lpw.tephra.storage.StorageListener;
import org.lpw.tephra.storage.Storages;
import org.lpw.tephra.util.Coder;
import org.lpw.tephra.util.Context;
import org.lpw.tephra.util.Converter;
import org.lpw.tephra.util.Io;
import org.lpw.tephra.util.Json;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Numeric;
import org.lpw.tephra.util.TimeHash;
import org.lpw.tephra.util.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lpw
 */
@Controller("tephra.ctrl.http.service.helper")
public class ServiceHelperImpl implements ServiceHelper, StorageListener {
    private static final String ROOT = "/";

    @Inject
    private Validator validator;
    @Inject
    private Converter converter;
    @Inject
    private Io io;
    @Inject
    private Json json;
    @Inject
    private Context context;
    @Inject
    private Numeric numeric;
    @Inject
    private Coder coder;
    @Inject
    private TimeHash timeHash;
    @Inject
    private Logger logger;
    @Inject
    private HeaderAware headerAware;
    @Inject
    private SessionAware sessionAware;
    @Inject
    private RequestAware requestAware;
    @Inject
    private ResponseAware responseAware;
    @Inject
    private Handler handler;
    @Inject
    private Dispatcher dispatcher;
    @Inject
    private Status status;
    @Inject
    private Redirect redirect;
    @Inject
    private Optional<IgnoreTimeHash> ignoreTimeHash;
    @Inject
    private CookieAware cookieAware;
    @Value("${tephra.ctrl.http.ignore.root:false}")
    private boolean ignoreRoot;
    @Value("${tephra.ctrl.http.ignore.prefixes:/upload/}")
    private String ignorePrefixes;
    @Value("${tephra.ctrl.http.ignore.suffixes:.ico,.js,.css,.html,.jpg,.jpeg,.gif,.png,.svg,.eot,.woff,.ttf,.txt}")
    private String ignoreSuffixes;
    @Value("${tephra.ctrl.http.cors:/WEB-INF/http/cors.json}")
    private String cors;
    @Value("${tephra.ctrl.http.virtual-context:}")
    private String virtualContext;
    private int contextPath;
    private String servletContextPath;
    private int virtualContextLength;
    private String[] prefixes;
    private String[] suffixes;
    private Set<String> ignoreUris;
    private Map<String, String> redirectMap = new ConcurrentHashMap<>();
    private Set<String> redirects = Collections.synchronizedSet(new HashSet<>());
    private Set<String> corsOrigins;
    private String corsMethods;
    private String corsHeaders;

    @Override
    public void setPath(String real, String context) {
        contextPath = validator.isEmpty(context) || context.equals(ROOT) ? 0 : context.length();
        servletContextPath = contextPath > 0 ? context : "";
        virtualContextLength = virtualContext.length();
        if (logger.isInfoEnable())
            logger.info("部署项目路径[{}]，虚拟路径[{}]。", context, virtualContext);
        prefixes = converter.toArray(ignorePrefixes, ",");
        suffixes = converter.toArray(ignoreSuffixes, ",");

        ignoreUris = new HashSet<>();
        BeanFactory.getBeans(IgnoreUri.class).forEach(ignoreUri -> ignoreUris.addAll(Arrays.asList(ignoreUri.getIgnoreUris())));
    }

    @Override
    public boolean service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getMethod().equals("OPTIONS")) {
            setCors(request, response);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);

            return true;
        }

        String uri = getUri(request);
        String lowerCaseUri = uri.toLowerCase();
        if (lowerCaseUri.startsWith(UploadService.ROOT)) {
            if (lowerCaseUri.startsWith(UploadService.ROOT + "image/"))
                return false;

            StringBuilder attachment = new StringBuilder("attachment; filename*=").append(context.getCharset(null)).append("''");
            String filename = request.getParameter("filename");
            if (validator.isEmpty(filename))
                attachment.append(uri.substring(uri.lastIndexOf('/') + 1));
            else {
                attachment.append(coder.encodeUrl(filename, null));
                int indexOf;
                if (filename.indexOf('.') == -1 && (indexOf = uri.lastIndexOf('.')) > -1)
                    attachment.append(uri.substring(indexOf));
            }
            response.setHeader("Content-Disposition", attachment.toString());
            if (logger.isDebugEnable())
                logger.debug("请求[{}]非图片上传资源。", uri);

            return false;
        }

        if (ignoreUris.contains(uri) || resource(request, response, uri)) {
            if (logger.isDebugEnable())
                logger.debug("忽略请求[{}]。", uri);

            return false;
        }

        if (redirect.redirect(request, uri, response))
            return true;

        context.clearThreadLocal();
        if (lowerCaseUri.equals(WsHelper.URI)) {
            context.putThreadLocal(WsHelper.IP, request.getRemoteAddr());
            context.putThreadLocal(WsHelper.PORT, request.getServerPort());

            return false;
        }

        String sessionId = getSessionId(request);
        try {
            return handler.call(sessionId, () -> service(request, response, uri, sessionId));
        } catch (Exception e) {
            logger.warn(e, "处理请求[{}]时发生异常！", uri);

            return false;
        }
    }

    private String getUri(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (contextPath > 0)
            uri = uri.substring(contextPath);
        if (virtualContextLength > 0 && uri.startsWith(virtualContext))
            uri = uri.substring(virtualContextLength);


        return uri;
    }

    private boolean service(HttpServletRequest request, HttpServletResponse response, String uri, String sessionId) throws IOException {
        setCors(request, response);
        OutputStream outputStream = setContext(request, response, uri, sessionId);
        if (timeHash.isEnable() && !timeHash.valid(request.getIntHeader("time-hash")) && !status.isStatus(uri)
                && (!ignoreTimeHash.isPresent() || !ignoreTimeHash.get().ignore())) {
            if (logger.isDebugEnable())
                logger.debug("请求[{}]TimeHash[{}]验证不通过。", uri, request.getIntHeader("time-hash"));

            return false;
        }

        dispatcher.execute();
        outputStream.flush();
        outputStream.close();

        return true;
    }

    private boolean resource(HttpServletRequest request, HttpServletResponse response, String uri) {
        if (!ignore(uri))
            return false;

        File file = new File(context.getAbsolutePath(uri));
        if (!file.exists()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);

            return true;
        }

        if (file.isDirectory())
            return true;

        response.setHeader("Cache-Control", "no-cache");
        String ifNoneMatch = request.getHeader("If-None-Match");
        String lastModified = numeric.toString(file.lastModified(), "0");
        if (lastModified.equals(ifNoneMatch))
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        else
            response.setHeader("ETag", lastModified);

        return true;
    }

    private boolean ignore(String uri) {
        if (ignoreRoot && uri.equals(ROOT))
            return true;

        for (String prefix : prefixes)
            if (uri.startsWith(prefix))
                return true;

        int indexOf = uri.lastIndexOf('/');
        if (indexOf == -1)
            return false;

        String name = uri.substring(indexOf + 1);
        for (String n : suffixes)
            if (name.equals(n))
                return true;

        indexOf = name.lastIndexOf('.');
        if (indexOf == -1)
            return false;

        String suffix = name.substring(indexOf);
        for (String s : suffixes)
            if (suffix.equals(s))
                return true;

        return false;
    }

    @Override
    public void setCors(HttpServletRequest request, HttpServletResponse response) {
        if (validator.isEmpty(corsOrigins))
            return;

        String origin = request.getHeader("Origin");
        if (!corsOrigins.contains("*") && !corsOrigins.contains(origin))
            return;

        response.addHeader("Access-Control-Allow-Origin", origin);
        response.addHeader("Access-Control-Allow-Methods", corsMethods);
        response.addHeader("Access-Control-Allow-Headers", corsHeaders);
        response.addHeader("Access-Control-Allow-Credentials", "true");
    }

    @Override
    public OutputStream setContext(HttpServletRequest request, HttpServletResponse response, String uri) throws IOException {
        return setContext(request, response, uri, getSessionId(request));
    }

    private OutputStream setContext(HttpServletRequest request, HttpServletResponse response, String uri, String sessionId) throws IOException {
        context.setLocale(request.getLocale());
        headerAware.set(new HeaderAdapterImpl(request));
        sessionAware.set(new SessionAdapterImpl(sessionId));
        requestAware.set(new RequestAdapterImpl(request, uri));
        cookieAware.set(request, response);
        response.setCharacterEncoding(context.getCharset(null));
        OutputStream outputStream = response.getOutputStream();
        responseAware.set(new ResponseAdapterImpl(servletContextPath, response, outputStream));

        return outputStream;
    }

    private String getSessionId(HttpServletRequest request) {
        String sessionId = request.getHeader(SESSION_ID);
        if (!validator.isEmpty(sessionId))
            return useTephraSessionId(request, sessionId);

        sessionId = request.getParameter(SESSION_ID);
        if (!validator.isEmpty(sessionId))
            return useTephraSessionId(request, sessionId);

        sessionId = converter.toString(request.getSession().getAttribute(SESSION_ID));
        if (!validator.isEmpty(sessionId))
            return sessionId;

        return request.getSession().getId();
    }

    private String useTephraSessionId(HttpServletRequest request, String sessionId) {
        request.getSession().setAttribute(SESSION_ID, sessionId);

        return sessionId;
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
