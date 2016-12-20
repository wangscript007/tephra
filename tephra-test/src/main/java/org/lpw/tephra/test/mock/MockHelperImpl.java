package org.lpw.tephra.test.mock;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.lpw.tephra.ctrl.Dispatcher;
import org.lpw.tephra.ctrl.context.HeaderAware;
import org.lpw.tephra.ctrl.context.RequestAware;
import org.lpw.tephra.ctrl.context.ResponseAware;
import org.lpw.tephra.ctrl.context.SessionAware;
import org.lpw.tephra.util.Context;
import org.lpw.tephra.util.Generator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * @author lpw
 */
@Aspect
@Controller("tephra.test.mock.helper")
public class MockHelperImpl implements MockHelper {
    @Autowired
    protected Context context;
    @Autowired
    protected Generator generator;
    @Autowired
    protected HeaderAware headerAware;
    @Autowired
    protected SessionAware sessionAware;
    @Autowired
    protected RequestAware requestAware;
    @Autowired
    protected ResponseAware responseAware;
    @Autowired
    protected Dispatcher dispatcher;
    protected ThreadLocal<MockHeader> header = new ThreadLocal<>();
    protected ThreadLocal<MockSession> session = new ThreadLocal<>();
    protected ThreadLocal<MockRequest> request = new ThreadLocal<>();
    protected ThreadLocal<MockResponse> response = new ThreadLocal<>();
    protected ThreadLocal<MockFreemarker> freemarker = new ThreadLocal<>();

    @Override
    public MockHeader getHeader() {
        if (header.get() == null)
            header.set(new MockHeaderImpl());

        return header.get();
    }

    @Override
    public MockSession getSession() {
        MockSession mockSession = session.get();
        if (mockSession == null) {
            mockSession = new MockSessionImpl();
            mockSession.setId(generator.random(32));
            session.set(mockSession);
        }

        return mockSession;
    }

    @Override
    public MockRequest getRequest() {
        if (request.get() == null)
            request.set(new MockRequestImpl());

        return request.get();
    }

    @Override
    public MockResponse getResponse() {
        if (response.get() == null)
            response.set(new MockResponseImpl());

        return response.get();
    }

    @Override
    public MockFreemarker getFreemarker() {
        return freemarker.get();
    }

    @Around("target(org.lpw.tephra.freemarker.Freemarker)")
    public Object process(ProceedingJoinPoint point) throws Throwable {
        if (getFreemarker() != null) {
            getFreemarker().process((String) point.getArgs()[0], point.getArgs()[1], null);

            return null;
        }

        return point.proceed();
    }

    @Override
    public void reset() {
        header.remove();
        session.remove();
        request.remove();
        response.remove();
        freemarker.remove();
    }

    @Override
    public void mock(String uri) {
        mock(uri, false);
    }

    @Override
    public void mock(String uri, boolean freemarker) {
        headerAware.set(getHeader());
        sessionAware.set(getSession());
        getRequest().setUri(uri);
        requestAware.set(getRequest());
        responseAware.set(getResponse());
        if (freemarker)
            this.freemarker.set(new MockFreemarkerImpl());
        dispatcher.execute();
    }
}
