package org.lpw.tephra.ctrl.socket;

import org.lpw.tephra.nio.ServerListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

/**
 * @author lpw
 */
@Controller("tephra.ctrl.socket.server-listener")
public class ServerListenerImpl implements ServerListener {
    @Value("${tephra.ctrl.socket.port:0}")
    private int port;
    @Value("${tephra.ctrl.socket.max-thread:64}")
    private int maxThread;

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
    }

    @Override
    public void receive(String sessionId, byte[] message) {
        int size = 0;
        for (int i = 0; i < 4; i++)
            size = (size << 8) + (message[i] & 0xff);
        if (message.length != size + 4)
            return;
    }

    @Override
    public void disconnect(String sessionId) {
    }
}
