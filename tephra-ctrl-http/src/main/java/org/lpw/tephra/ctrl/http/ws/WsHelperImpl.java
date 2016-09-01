package org.lpw.tephra.ctrl.http.ws;

import org.lpw.tephra.bean.ContextClosedListener;
import org.lpw.tephra.bean.ContextRefreshedListener;
import org.lpw.tephra.ctrl.http.IgnoreUri;
import org.lpw.tephra.dao.Commitable;
import org.lpw.tephra.util.Generator;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Security;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.websocket.Session;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author lpw
 */
@Service("tephra.ctrl.http.ws.helper")
public class WsHelperImpl implements WsHelper, IgnoreUri, ContextRefreshedListener, ContextClosedListener {
    @Autowired
    protected Generator generator;
    @Autowired
    protected Security security;
    @Autowired
    protected Logger logger;
    @Autowired
    protected Set<Commitable> commitables;
    @Autowired(required = false)
    protected WsListener listener;
    @Value("${tephra.ctrl.http.web-socket.max:64}")
    protected int max;
    protected AtomicLong counter;
    protected Map<String, Session> sessions;
    protected String key;

    @Override
    public void open(Session session) {
        if (listener == null || counter.incrementAndGet() > max) {
            logger.warn(null, listener == null ? "未提供WsListener实现，无法开启WebSocket监听。" : "超过最大可连接数，拒绝新连接。");
            try {
                session.close();
            } catch (IOException e) {
                logger.warn(e, "关闭客户端Session时发生异常！");
            }

            return;
        }

        System.out.println("##count:"+counter.get());

        String key = getKey(session);
        sessions.put(key, session);
        listener.open(key);
        commitables.forEach(Commitable::close);
    }

    @Override
    public void message(Session session, String message) {
        if (listener == null)
            return;

        listener.message(getKey(session), message);
        commitables.forEach(Commitable::close);
    }

    @Override
    public void error(Session session, Throwable throwable) {
        if (listener == null)
            return;

        String key = getKey(session);
        listener.error(key, throwable);
        logger.warn(throwable, "WebSocket执行异常！");
        commitables.forEach(Commitable::close);
    }

    @Override
    public void close(Session session) {
        if (listener == null)
            return;

        String key = getKey(session);
        listener.close(key);
        sessions.remove(key);
        commitables.forEach(Commitable::close);
        counter.decrementAndGet();
    }

    protected String getKey(Session session) {
        return security.md5(key + session.getId());
    }

    @Override
    public void send(String sessionId, String message) {
        if (listener == null)
            return;

        Session session = sessions.get(sessionId);
        if (session == null) {
            logger.warn(null, "Session ID[{}]不存在！", sessionId);

            return;
        }

        send(session, message);
    }

    @Override
    public void send(String message) {
        if (listener == null)
            return;

        sessions.values().forEach(session -> send(session, message));
    }

    protected void send(Session session, String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            logger.warn(e, "发送信息[{}]时发生异常！", message);
        }
    }

    @Override
    public void close(String sessionId) {
        Session session = sessions.get(sessionId);
        if (session != null) {
            try {
                session.close();
            } catch (IOException e) {
                logger.warn(e, "关闭客户端Session[{}]时发生异常！", sessionId);
            }
        }
    }

    @Override
    public void close() {
        sessions.keySet().forEach(this::close);
    }

    @Override
    public String[] getIgnoreUris() {
        return new String[]{URI};
    }

    @Override
    public int getContextRefreshedSort() {
        return 18;
    }

    @Override
    public void onContextRefreshed() {
        counter = new AtomicLong();
        sessions = new ConcurrentHashMap<>();
        key = generator.random(32);
    }

    @Override
    public int getContextClosedSort() {
        return 18;
    }

    @Override
    public void onContextClosed() {
        close();
    }
}
