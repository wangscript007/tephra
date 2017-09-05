package org.lpw.tephra.ctrl.http;

import org.lpw.tephra.bean.BeanFactory;
import org.lpw.tephra.ctrl.Dispatcher;
import org.lpw.tephra.ctrl.context.HeaderAware;
import org.lpw.tephra.ctrl.context.RequestAware;
import org.lpw.tephra.ctrl.context.ResponseAware;
import org.lpw.tephra.ctrl.context.SessionAware;
import org.lpw.tephra.ctrl.http.context.CookieAware;
import org.lpw.tephra.ctrl.http.context.HeaderAdapterImpl;
import org.lpw.tephra.ctrl.http.context.RequestAdapterImpl;
import org.lpw.tephra.ctrl.http.context.ResponseAdapterImpl;
import org.lpw.tephra.ctrl.http.context.SessionAdapterImpl;
import org.lpw.tephra.ctrl.http.upload.UploadHelper;
import org.lpw.tephra.ctrl.status.Status;
import org.lpw.tephra.storage.StorageListener;
import org.lpw.tephra.storage.Storages;
import org.lpw.tephra.util.Context;
import org.lpw.tephra.util.Converter;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.TimeHash;
import org.lpw.tephra.util.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * @author lpw
 */
@Controller("tephra.ctrl.http.service.helper")
public class ServiceHelperImpl implements ServiceHelper, StorageListener {
    private static final String ROOT = "/";
    private static final String SESSION_ID = "tephra-session-id";

    @Inject
    private Validator validator;
    @Inject
    private Converter converter;
    @Inject
    private Context context;
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
    private Dispatcher dispatcher;
    @Inject
    private Status status;
    @Inject
    private Optional<IgnoreTimeHash> ignoreTimeHash;
    @Inject
    private CookieAware cookieAware;
    @Value("${tephra.ctrl.http.ignor.root:false}")
    private boolean ignorRoot;
    @Value("${tephra.ctrl.http.ignor.prefixes:/upload/}")
    private String ignorPrefixes;
    @Value("${tephra.ctrl.http.ignor.names:}")
    private String ignorNames;
    @Value("${tephra.ctrl.http.ignor.suffixes:.ico,.js,.css,.html,.jpg,.jpeg,.gif,.png}")
    private String ignorSuffixes;
    @Value("${tephra.ctrl.http.cors:false}")
    private boolean cors;
    @Value("${tephra.ctrl.http.cors.headers:/WEB-INF/cors-headers}")
    private String corsHeaders;
    private int contextPath;
    private String servletContextPath;
    private String[] prefixes;
    private String[] suffixes;
    private Set<String> ignoreUris;
    private String headers;

    @Override
    public void setPath(String real, String context) {
        contextPath = validator.isEmpty(context) || context.equals(ROOT) ? 0 : context.length();
        servletContextPath = contextPath > 0 ? context : "";
        if (logger.isInfoEnable())
            logger.info("部署项目路径[{}]。", context);
        if (logger.isInfoEnable())
            logger.info("跨域设置[{}]。", cors);
        prefixes = converter.toArray(ignorPrefixes, ",");
        suffixes = converter.toArray(ignorSuffixes, ",");

        ignoreUris = new HashSet<>();
        BeanFactory.getBeans(IgnoreUri.class).forEach(ignoreUri -> ignoreUris.addAll(Arrays.asList(ignoreUri.getIgnoreUris())));
    }

    @Override
    public boolean service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String uri = request.getRequestURI();
        if (contextPath > 0)
            uri = uri.substring(contextPath);
        String lowerCaseUri = uri.toLowerCase();
        if (lowerCaseUri.startsWith(UploadHelper.ROOT)) {
            if (!lowerCaseUri.startsWith(UploadHelper.ROOT + "image/"))
                response.setHeader("Content-Disposition", "attachment;filename=" + uri.substring(uri.lastIndexOf('/') + 1));

            if (logger.isDebugEnable())
                logger.debug("请求[{}]非图片上传资源。", uri);

            return false;
        }

        if (ignoreUris.contains(uri) || ignore(uri)) {
            if (logger.isDebugEnable())
                logger.debug("忽略请求[{}]。", uri);

            return false;
        }

        setCors(response);
        OutputStream outputStream = setContext(request, response, uri);
        if (timeHash.isEnable() && !timeHash.valid(request.getIntHeader("time-hash")) && !status.isStatus(uri) && (!ignoreTimeHash.isPresent() || !ignoreTimeHash.get().ignore())) {
            if (logger.isDebugEnable())
                logger.debug("请求[{}]TimeHash[{}]验证不通过。", uri, request.getIntHeader("time-hash"));

            return false;
        }

        dispatcher.execute();
        outputStream.flush();
        outputStream.close();

        return true;
    }

    private boolean ignore(String uri) {
        if (ignorRoot && uri.equals(ROOT))
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

    private void setCors(HttpServletResponse response) {
        if (!cors)
            return;

        response.addHeader("Access-Control-Allow-Origin", "*");
        if (!validator.isEmpty(headers))
            response.addHeader("Access-Control-Allow-Headers", headers);
    }

    public OutputStream setContext(HttpServletRequest request, HttpServletResponse response, String uri) throws IOException {
        context.setLocale(request.getLocale());
        headerAware.set(new HeaderAdapterImpl(request));
        sessionAware.set(new SessionAdapterImpl(getSessionId(request)));
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
        return new String[]{corsHeaders};
    }

    @Override
    public void onStorageChanged(String path, String absolutePath) {
        try {
            Set<String> set = new HashSet<>();
            BufferedReader reader = new BufferedReader(new FileReader(absolutePath));
            for (String line; (line = reader.readLine()) != null; ) {
                line = line.trim();
                if (line.length() == 0 || line.indexOf('#') == 0)
                    continue;

                set.add(line);
            }
            reader.close();
            set.add(SESSION_ID);
            headers = converter.toString(set);
            if (logger.isInfoEnable())
                logger.info("设置跨域允许的请求头[{}]。", headers);
        } catch (Throwable throwable) {
            logger.warn(throwable, "读取跨域头配置[{}]发生异常！", absolutePath);
        }
    }
}
