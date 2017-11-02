package org.lpw.tephra.aio;

import org.lpw.tephra.bean.BeanFactory;
import org.lpw.tephra.util.Logger;

import javax.inject.Inject;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

/**
 * @author lpw
 */
class HandlerSupport {
    @Inject
    Logger logger;
    @Inject
    AioHelper aioHelper;
    int port;

    void read(AsynchronousSocketChannel socketChannel, String sessionId, AioListener listener) {
        ByteBuffer buffer = (ByteBuffer) ByteBuffer.allocate(4096).clear();
        socketChannel.read(buffer, null, BeanFactory.getBean(ReceiveHandler.class).bind(socketChannel, buffer, sessionId, listener));
    }
}
