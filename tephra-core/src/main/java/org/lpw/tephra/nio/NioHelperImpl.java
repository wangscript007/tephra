package org.lpw.tephra.nio;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.lpw.tephra.bean.ContextClosedListener;
import org.lpw.tephra.scheduler.MinuteJob;
import org.lpw.tephra.util.Logger;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lpw
 */
@Component("tephra.nio.helper")
public class NioHelperImpl implements NioHelper, ContextClosedListener, MinuteJob {
    @Inject
    private Logger logger;
    private Map<String, ChannelHandlerContext> map = new ConcurrentHashMap<>();

    @Override
    public String put(ChannelHandlerContext context) {
        String sessionId = getSessionId(context);
        map.put(sessionId, context);

        return sessionId;
    }

    @Override
    public String getSessionId(ChannelHandlerContext context) {
        return context.channel().id().asLongText();
    }

    @Override
    public String getIp(String sessionId) {
        ChannelHandlerContext context = map.get(sessionId);

        return context == null ? null : ((InetSocketAddress) context.channel()).getAddress().getHostAddress();
    }

    @Override
    public byte[] read(Object message) {
        ByteBuf buffer = (ByteBuf) message;
        byte[] bytes = new byte[buffer.readableBytes()];
        buffer.readBytes(bytes);

        return bytes;
    }

    @Override
    public void send(String sessionId, byte[] message) {
        ChannelHandlerContext context = map.get(sessionId);
        if (context == null)
            return;

        try {
            ByteBuf buffer = context.alloc().buffer(message.length);
            buffer.writeBytes(message);
            context.writeAndFlush(buffer).sync();
        } catch (InterruptedException e) {
            logger.warn(e, "发送数据[{}]时发生异常！", new String(message));
        }
    }

    @Override
    public void close(String sessionId) {
        if (sessionId != null && map.containsKey(sessionId))
            map.remove(sessionId).close();
    }

    @Override
    public int getContextClosedSort() {
        return 6;
    }

    @Override
    public void onContextClosed() {
        map.forEach((key, context) -> {
            if (context != null)
                context.close();
        });
    }

    @Override
    public void executeMinuteJob() {
        Set<String> set = new HashSet<>();
        map.forEach((key, value) -> {
            if (value == null)
                set.add(key);
        });
        set.forEach(map::remove);
    }
}
