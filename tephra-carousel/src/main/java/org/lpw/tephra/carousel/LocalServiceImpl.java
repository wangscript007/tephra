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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author lpw
 */
@Component("tephra.carousel.local-service")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class LocalServiceImpl implements LocalService {
    @Autowired
    protected HeaderAware headerAware;
    @Autowired
    protected RequestAware requestAware;
    @Autowired
    protected SessionAware sessionAware;
    @Autowired
    protected ResponseAware responseAware;
    @Autowired
    protected Session session;
    @Autowired
    protected Dispatcher dispatcher;
    @Autowired
    protected Response response;
    protected String uri;
    protected String ip;
    protected String sessionId;
    protected Map<String, String> header;
    protected Map<String, String> parameter;

    @Override
    public LocalService build(String uri, String ip, String sessionId, Map<String, String> header, Map<String, String> parameter) {
        this.uri = uri;
        this.ip = ip;
        this.sessionId = sessionId;
        this.header = header;
        this.parameter = parameter;

        return this;
    }

    @Override
    public String call() throws Exception {
        headerAware.set(new LocalHeaderAdapter(ip, header));
        requestAware.set(new LocalRequestAdapter(uri, parameter));
        sessionAware.set(new LocalSessionAdapter(sessionId));
        responseAware.set(new LocalResponseAdapter());
        dispatcher.execute();
        response.getOutputStream().flush();
        response.getOutputStream().close();

        return response.getOutputStream().toString();
    }
}
