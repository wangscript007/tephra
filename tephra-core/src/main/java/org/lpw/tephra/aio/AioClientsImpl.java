package org.lpw.tephra.aio;

import org.lpw.tephra.bean.BeanFactory;
import org.lpw.tephra.bean.ContextClosedListener;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author lpw
 */
@Component("tephra.aio.clients")
public class AioClientsImpl implements AioClients, ContextClosedListener {
    private Set<AioClient> set = Collections.synchronizedSet(new HashSet<>());

    @Override
    public AioClient get() {
        AioClient client = BeanFactory.getBean(AioClient.class);
        set.add(client);

        return client;
    }

    @Override
    public void close() {
        set.forEach(AioClient::close);
    }

    @Override
    public int getContextClosedSort() {
        return 5;
    }

    @Override
    public void onContextClosed() {
        close();
    }
}
