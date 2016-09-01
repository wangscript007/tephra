package org.lpw.tephra.cache.lr;

import org.lpw.tephra.nio.NioHelper;
import org.lpw.tephra.nio.ServerListener;
import org.lpw.tephra.util.Serializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author lpw
 */
@Component("tephra.cache.lr.listener")
public class ListenerImpl implements ServerListener {
    @Autowired
    protected Serializer serializer;
    @Autowired
    protected NioHelper nioHelper;
    @Autowired
    protected Local local;
    @Autowired
    protected Remote remote;
    @Value("${tephra.cache.listen-port:0}")
    protected int port;
    @Value("${tephra.cache.listener.max-thread:5}")
    protected int maxThread;

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public int getMaxThread() {
        return maxThread;
    }

    @Override
    public void accept(String sessionId) {
        nioHelper.send(sessionId, remote.getId().getBytes());
    }

    @Override
    public void receive(String sessionId, byte[] message) {
        if (message == null)
            return;

        Object object = serializer.unserialize(message);
        if (object == null)
            return;

        if (object instanceof String)
            local.remove((String) object);
        else if (object instanceof Element)
            local.put((Element) object);
    }

    @Override
    public void disconnect(String sessionId) {
    }
}
