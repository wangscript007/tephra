package org.lpw.tephra.ctrl.context;

import org.lpw.tephra.util.Numeric;
import org.lpw.tephra.util.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lpw
 */
@Controller("tephra.ctrl.context.header")
public class HeaderImpl implements Header, HeaderAware {
    @Inject
    private Validator validator;
    @Inject
    private Numeric numeric;
    @Value("${tephra.ctrl.context.header.real-ip:}")
    private String realIp;
    private ThreadLocal<HeaderAdapter> adapter = new ThreadLocal<>();

    @Override
    public String get(String name) {
        return getMap().get(name);
    }

    @Override
    public int getAsInt(String name) {
        return numeric.toInt(get(name));
    }

    @Override
    public long getAsLong(String name) {
        return numeric.toLong(get(name));
    }

    @Override
    public String getIp() {
        if (!validator.isEmpty(realIp)) {
            String ip = get(realIp);
            if (!validator.isEmpty(ip))
                return ip;
        }

        HeaderAdapter adapter = this.adapter.get();

        return adapter == null ? null : adapter.getIp();
    }

    @Override
    public Map<String, String> getMap() {
        HeaderAdapter adapter = this.adapter.get();

        return adapter == null ? new HashMap<>() : adapter.getMap();
    }

    @Override
    public void set(HeaderAdapter adapter) {
        this.adapter.set(adapter);
    }
}
