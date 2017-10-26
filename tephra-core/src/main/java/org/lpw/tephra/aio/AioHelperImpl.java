package org.lpw.tephra.aio;

import org.lpw.tephra.util.Logger;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lpw
 */
@Component("tephra.aio.helper")
public class AioHelperImpl implements AioHelper {
    @Inject
    private Logger logger;
    private Map<String, AsynchronousSocketChannel> map = new ConcurrentHashMap<>();

    @Override
    public String put(AsynchronousSocketChannel socketChannel) {
        String sessionId = getSessionId(socketChannel);
        map.put(sessionId, socketChannel);

        return sessionId;
    }

    @Override
    public String getSessionId(AsynchronousSocketChannel socketChannel) {
        return socketChannel.toString();
    }

    @Override
    public void send(String sessionId, byte[] message) {
        if (!map.containsKey(sessionId))
            return;

        map.get(sessionId).write(ByteBuffer.wrap(message));
    }

    @Override
    public void close(String sessionId) {
        if (!map.containsKey(sessionId))
            return;

        try {
            map.remove(sessionId).close();
        } catch (IOException e) {
            logger.warn(e, "关闭AIO Socket Channel[{}]时发生异常！", sessionId);
        }
    }
}
