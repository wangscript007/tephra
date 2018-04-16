package org.lpw.tephra.carousel;

import org.lpw.tephra.ctrl.Dispatcher;
import org.lpw.tephra.ctrl.context.HeaderAware;
import org.lpw.tephra.ctrl.context.LocalHeaderAdapter;
import org.lpw.tephra.ctrl.context.LocalRequestAdapter;
import org.lpw.tephra.ctrl.context.LocalResponseAdapter;
import org.lpw.tephra.ctrl.context.LocalSessionAdapter;
import org.lpw.tephra.ctrl.context.RequestAware;
import org.lpw.tephra.ctrl.context.Response;
import org.lpw.tephra.ctrl.context.ResponseAware;
import org.lpw.tephra.ctrl.context.Session;
import org.lpw.tephra.ctrl.context.SessionAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Map;

/**
 * @author lpw
 */
@Component("tephra.carousel.local-service")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class LocalServiceImpl implements LocalService {
    private static final String SESSION_ID = "tephra-session-id";

    @Inject
    private HeaderAware headerAware;
    @Inject
    private RequestAware requestAware;
    @Inject
    private SessionAware sessionAware;
    @Inject
    private Session session;
    @Inject
    private ResponseAware responseAware;
    @Inject
    private Dispatcher dispatcher;
    @Inject
    private Response response;
    private String uri;
    private String ip;
    private Map<String, String> header;
    private Map<String, String> parameter;

    @Override
    public LocalService build(String uri, String ip, Map<String, String> header, Map<String, String> parameter) {
        this.uri = uri;
        this.ip = ip;
        this.header = header;
        this.parameter = parameter;

        return this;
    }

    @Override
    public String call() throws Exception {
        headerAware.set(new LocalHeaderAdapter(ip, header));
        requestAware.set(new LocalRequestAdapter(uri, parameter));
        sessionAware.set(new LocalSessionAdapter(header != null && header.containsKey(SESSION_ID) ? header.get(SESSION_ID) : session.getId()));
        responseAware.set(new LocalResponseAdapter());
        dispatcher.execute();
        response.getOutputStream().flush();
        response.getOutputStream().close();

        return response.getOutputStream().toString();
    }
}
