package org.lpw.tephra.ctrl.http;

import org.lpw.tephra.bean.BeanFactory;
import org.lpw.tephra.ctrl.Dispatcher;
import org.lpw.tephra.ctrl.context.HeaderAware;
import org.lpw.tephra.ctrl.context.RequestAware;
import org.lpw.tephra.ctrl.context.SessionAware;
import org.lpw.tephra.ctrl.http.context.*;
import org.lpw.tephra.ctrl.http.upload.UploadHelper;
import org.lpw.tephra.ctrl.status.Status;
import org.lpw.tephra.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author lpw
 */
@Controller("tephra.ctrl.http.service.helper")
public class ServiceHelperImpl implements ServiceHelper {
    private static final String ROOT = "/";
    private static final String SESSION_ID = "tephra-session-id";

    @Autowired
    protected Validator validator;
    @Autowired
    protected Converter converter;
    @Autowired
    protected Context context;
    @Autowired
    protected TimeHash timeHash;
    @Autowired
    protected Generator generator;
    @Autowired
    protected Logger logger;
    @Autowired
    protected HeaderAware headerAware;
    @Autowired
    protected SessionAware sessionAware;
    @Autowired
    protected RequestAware requestAware;
    @Autowired
    protected Dispatcher dispatcher;
    @Autowired
    protected Status status;
    @Autowired(required = false)
    protected IgnoreTimeHash ignoreTimeHash;
    @Autowired
    protected CookieAware cookieAware;
    @Value("${tephra.ctrl.http.ignor.root:false}")
    protected boolean ignorRoot;
    @Value("${tephra.ctrl.http.ignor.prefixes:/upload/}")
    protected String ignorPrefixes;
    @Value("${tephra.ctrl.http.ignor.names:}")
    protected String ignorNames;
    @Value("${tephra.ctrl.http.ignor.suffixes:.ico,.js,.css,.html}")
    protected String ignorSuffixes;
    protected int contextPath;
    protected String servletContextPath;
    protected String[] prefixes;
    protected String[] names;
    protected String[] suffixes;
    protected Set<String> ignoreUris;

    @Override
    public void setPath(String real, String context) {
        this.context.setRoot(real);
        contextPath = validator.isEmpty(context) || context.equals(ROOT) ? 0 : context.length();
        servletContextPath = contextPath > 0 ? context : "";
        if (logger.isInfoEnable())
            logger.info("部署项目路径[{}]。", context);
        prefixes = converter.toArray(ignorPrefixes, ",");
        names = converter.toArray(ignorNames, ",");
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

        if (ignoreUris.contains(uri) || ignor(uri)) {
            if (logger.isDebugEnable())
                logger.debug("忽略请求[{}]。", uri);

            return false;
        }

        setContext(request, response, uri);
        if (timeHash.isEnable() && !timeHash.valid(request.getIntHeader("time-hash")) && !status.isStatus(uri) && (ignoreTimeHash == null || !ignoreTimeHash.ignore())) {
            if (logger.isDebugEnable())
                logger.debug("请求[{}]TimeHash[{}]验证不通过。", uri, request.getIntHeader("time-hash"));

            return false;
        }

        response.setCharacterEncoding("UTF-8");
        OutputStream output = response.getOutputStream();
        dispatcher.execute(new ResponseImpl(servletContextPath, response, output));
        output.flush();
        output.close();

        return true;
    }

    protected boolean ignor(String uri) {
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

    @Override
    public void setContext(HttpServletRequest request, HttpServletResponse response, String uri) {
        context.setLocale(request.getLocale());
        headerAware.set(new HeaderAdapterImpl(request));
        sessionAware.set(new SessionAdapterImpl(getSessionId(request)));
        requestAware.set(new RequestAdapterImpl(request, uri));
        cookieAware.set(request, response);
    }

    protected String getSessionId(HttpServletRequest request) {
        String sessionId = request.getHeader("tephra-session-id");
        if (!validator.isEmpty(sessionId))
            return sessionId;

        return request.getSession().getId();
    }
}
