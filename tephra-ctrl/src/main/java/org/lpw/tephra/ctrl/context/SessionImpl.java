package org.lpw.tephra.ctrl.context;

import org.lpw.tephra.cache.Cache;
import org.lpw.tephra.util.Context;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

/**
 * @author lpw
 */
@Controller("tephra.ctrl.context.session")
public class SessionImpl implements Session, SessionAware {
    private static final String ADAPTER = "tephra.ctrl.context.session.adapter";
    private static final String CACHE = "tephra.ctrl.context.session:";

    @Inject
    private Context context;
    @Inject
    private Cache cache;

    @Override
    public void set(String key, Object value) {
        set(getId(), key, value);
    }

    @Override
    public void set(String id, String key, Object value) {
        String cacheKey = getCacheKey(id, key);
        context.putThreadLocal(cacheKey, value);
        cache.put(cacheKey, value, false);
    }

    @Override
    public <T> T get(String key) {
        return get(getId(), key);
    }

    @Override
    public <T> T get(String id, String key) {
        String cacheKey = getCacheKey(id, key);
        T value = context.getThreadLocal(cacheKey);

        return value == null ? cache.get(cacheKey) : value;
    }

    @Override
    public void remove(String key) {
        remove(getId(), key);
    }

    @Override
    public void remove(String id, String key) {
        String cacheKey = getCacheKey(id, key);
        context.removeThreadLocal(cacheKey);
        cache.remove(cacheKey);
    }

    private String getCacheKey(String id, String key) {
        return CACHE + id + key;
    }

    @Override
    public String getId() {
        SessionAdapter adapter = context.getThreadLocal(ADAPTER);

        return adapter == null ? null : adapter.getId();
    }

    @Override
    public void set(SessionAdapter adapter) {
        context.putThreadLocal(ADAPTER, adapter);
    }
}
