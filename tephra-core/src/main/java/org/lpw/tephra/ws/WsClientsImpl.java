package org.lpw.tephra.ws;

import org.lpw.tephra.bean.BeanFactory;
import org.lpw.tephra.bean.ContextClosedListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author lpw
 */
@Component("tephra.ws.clients")
public class WsClientsImpl implements WsClients, ContextClosedListener {
    @Value("${tephra.ws.client.max-size:67108864}")
    private int maxSize;
    private WebSocketContainer container;
    private Set<WsClient> clients = Collections.synchronizedSet(new HashSet<>());

    @Override
    public WsClient get() {
        WsClient client = BeanFactory.getBean(WsClient.class).setContainer(getContainer());
        clients.add(client);

        return client;
    }

    private synchronized WebSocketContainer getContainer() {
        if (container == null) {
            container = ContainerProvider.getWebSocketContainer();
            container.setDefaultMaxTextMessageBufferSize(maxSize);
        }

        return container;
    }

    @Override
    public void close() {
        if (clients.isEmpty())
            return;

        clients.forEach(WsClient::close);
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
