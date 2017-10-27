package org.lpw.tephra.aio;

import org.lpw.tephra.scheduler.SecondsJob;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.TimeUnit;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lpw
 */
@Component("tephra.aio.handler.receive")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ReceiveHandlerImpl implements ReceiveHandler, SecondsJob {
    @Inject
    private Logger logger;
    private Map<String, ByteArrayOutputStream> outputStream = new ConcurrentHashMap<>();
    private Map<String, Long> time = new ConcurrentHashMap<>();
    private AsynchronousSocketChannel socketChannel;
    private ByteBuffer buffer;
    private String sessionId;
    private AioListener listener;

    @Override
    public ReceiveHandler bind(AsynchronousSocketChannel socketChannel, ByteBuffer buffer, String sessionId, AioListener listener) {
        this.socketChannel = socketChannel;
        this.buffer = buffer;
        this.sessionId = sessionId;
        this.listener = listener;

        return this;
    }

    @Override
    public void completed(Integer integer, Object object) {
        if (integer == -1) {
            if (logger.isDebugEnable())
                logger.debug("AIO连接[{}]断开。", socketChannel);

            try {
                listener.disconnect(sessionId);
                socketChannel.close();
            } catch (IOException e) {
                logger.warn(e, "关闭AIO[{}]连接时发生异常！", socketChannel);
            }

            return;
        }

        buffer.flip();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        buffer.clear();
        read(sessionId, bytes, bytes.length < buffer.capacity());
        socketChannel.read(buffer, null, this);
    }

    private void read(String sessionId, byte[] bytes, boolean finish) {
        if (finish && !outputStream.containsKey(sessionId)) {
            listener.receive(sessionId, bytes);

            return;
        }

        outputStream.computeIfAbsent(sessionId, sid -> new ByteArrayOutputStream()).write(bytes, 0, bytes.length);
        if (finish) {
            read(sessionId);

            return;
        }

        time.put(sessionId, System.currentTimeMillis());
    }

    private void read(String sessionId) {
        try {
            ByteArrayOutputStream outputStream = this.outputStream.remove(sessionId);
            time.remove(sessionId);
            outputStream.close();
            listener.receive(sessionId, outputStream.toByteArray());
        } catch (IOException e) {
            logger.warn(null, "读取AIO[{}]数据时发生异常！", sessionId);
        }
    }

    @Override
    public void failed(Throwable throwable, Object object) {
        logger.warn(throwable, "监听AIO[{}]数据时发生异常！", socketChannel);
    }

    @Override
    public void executeSecondsJob() {
        Set<String> set = new HashSet<>();
        time.forEach((sessionId, time) -> {
            if (System.currentTimeMillis() - time > TimeUnit.Second.getTime())
                set.add(sessionId);
        });
        set.forEach(this::read);
    }
}
