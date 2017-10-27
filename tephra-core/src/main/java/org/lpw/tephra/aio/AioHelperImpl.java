package org.lpw.tephra.aio;

import org.lpw.tephra.scheduler.MinuteJob;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Thread;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author lpw
 */
@Component("tephra.aio.helper")
public class AioHelperImpl implements AioHelper, MinuteJob {
    @Inject
    private Thread thread;
    @Inject
    private Logger logger;
    private Map<String, AsynchronousSocketChannel> map = new ConcurrentHashMap<>();
    private Map<String, ExecutorService> services = new ConcurrentHashMap<>();

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

        services.computeIfAbsent(sessionId, sid -> Executors.newFixedThreadPool(1))
                .submit(() -> map.get(sessionId).write(ByteBuffer.wrap(message)));
    }

    @Override
    public void close(String sessionId) {
        if (!map.containsKey(sessionId))
            return;

        try {
            map.remove(sessionId).close();
            services.remove(sessionId).shutdown();
        } catch (IOException e) {
            logger.warn(e, "关闭AIO Socket Channel[{}]时发生异常！", sessionId);
        }
    }

    @Override
    public void executeMinuteJob() {
        Set<String> set = new HashSet<>();
        map.forEach((sessionId, socketChannel) -> {
            if (socketChannel.isOpen())
                return;

            set.add(sessionId);
        });
        set.forEach(this::close);
    }
}