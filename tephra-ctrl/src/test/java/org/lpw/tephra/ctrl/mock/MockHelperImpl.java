package org.lpw.tephra.ctrl.mock;

import org.lpw.tephra.ctrl.Dispatcher;
import org.lpw.tephra.ctrl.context.HeaderAware;
import org.lpw.tephra.ctrl.context.RequestAware;
import org.lpw.tephra.ctrl.context.SessionAware;
import org.lpw.tephra.util.Context;
import org.lpw.tephra.util.Generator;
import org.lpw.tephra.util.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.IOException;

/**
 * @author lpw
 */
@Controller("tephra.test.mock.helper")
public class MockHelperImpl implements MockHelper {
    @Autowired
    protected Context context;
    @Autowired
    protected Validator validator;
    @Autowired
    protected Generator generator;
    @Autowired
    protected HeaderAware headerAware;
    @Autowired
    protected SessionAware sessionAware;
    @Autowired
    protected RequestAware requestAware;
    @Autowired
    protected Dispatcher dispatcher;
    protected ThreadLocal<MockHeader> header = new ThreadLocal<>();
    protected ThreadLocal<MockSession> session = new ThreadLocal<>();
    protected ThreadLocal<MockRequest> request = new ThreadLocal<>();

    @Override
    public MockHeader getHeader() {
        if (header.get() == null)
            header.set(new MockHeaderImpl());

        return header.get();
    }

    @Override
    public MockSession getSession() {
        if (session.get() == null)
            session.set(new MockSessionImpl());

        return session.get();
    }

    @Override
    public MockRequest getRequest() {
        if (request.get() == null)
            request.set(new MockRequestImpl());

        return request.get();
    }

    @Override
    public MockResponse mock(String uri) {
        headerAware.set(getHeader());
        sessionAware.set(getSession());
        getRequest().setUri(uri);
        requestAware.set(getRequest());

        MockResponse response = new MockResponseImpl();
        dispatcher.execute(response);

        return response;
    }

    @Override
    public MockResponse mock(String web, String uri) {
        try {
            context.setRoot(new File(web).getCanonicalPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return mock(uri);
    }
}
