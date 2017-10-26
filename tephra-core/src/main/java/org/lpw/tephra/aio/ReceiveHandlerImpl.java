package org.lpw.tephra.aio;

import org.lpw.tephra.util.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

/**
 * @author lpw
 */
@Component("tephra.aio.handler.receive")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ReceiveHandlerImpl implements ReceiveHandler {
    @Inject
    private Logger logger;
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
        if (integer < 1) {
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
        listener.receive(sessionId, bytes);
        socketChannel.read(buffer, null, this);
    }

    @Override
    public void failed(Throwable throwable, Object object) {
        logger.warn(throwable, "监听AIO[{}]数据时发生异常！", socketChannel);
    }
}
