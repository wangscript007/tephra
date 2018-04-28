package org.lpw.tephra.ctrl.context;

import org.lpw.tephra.util.Context;
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
    private static final String ADAPTER = "tephra.ctrl.context.header.adapter";
    private static final String IP = "tephra.ctrl.context.header.ip";

    @Inject
    private Context context;
    @Inject
    private Validator validator;
    @Inject
    private Numeric numeric;
    @Value("${tephra.ctrl.context.header.real-ip:}")
    private String realIp;

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
        String ip = context.getThreadLocal(IP);
        if (!validator.isEmpty(ip))
            return ip;

        if (!validator.isEmpty(realIp)) {
            ip = get(realIp);
            if (!validator.isEmpty(ip))
                return ip;
        }

        HeaderAdapter adapter = context.getThreadLocal(ADAPTER);

        return adapter == null ? null : adapter.getIp();
    }

    @Override
    public void setIp(String ip) {
        context.putThreadLocal(IP, ip);
    }

    @Override
    public Map<String, String> getMap() {
        HeaderAdapter adapter = context.getThreadLocal(ADAPTER);

        return adapter == null ? new HashMap<>() : adapter.getMap();
    }

    @Override
    public void set(HeaderAdapter adapter) {
        context.putThreadLocal(ADAPTER, adapter);
    }
}
