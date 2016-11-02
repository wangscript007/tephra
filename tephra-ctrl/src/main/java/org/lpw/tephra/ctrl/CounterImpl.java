package org.lpw.tephra.ctrl;

import org.lpw.tephra.util.Converter;
import org.lpw.tephra.util.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @auth lpw
 */
@Controller("tephra.ctrl.counter")
public class CounterImpl implements Counter {
    @Autowired
    protected Converter converter;
    @Autowired
    protected Logger logger;
    @Autowired
    protected TrustfulIp trustfulIp;
    @Value("${tephra.ctrl.counter.max:512}")
    protected int max;
    @Value("${tephra.ctrl.counter.ip-max:5}")
    protected int ipMax;
    protected AtomicInteger counter = new AtomicInteger();
    protected Map<String, Integer> ipMap = new HashMap<>();

    @Override
    public boolean increase(String ip) {
        if (counter.incrementAndGet() >= max) {
            logger.warn(null, "超过最大并发处理数[{}]。", max);

            return false;
        }

        if (trustfulIp.contains(ip))
            return true;

        int n = converter.toInt(ipMap.get(ip)) + 1;
        if (n > ipMax) {
            logger.warn(null, "超过IP[{}]最大并发处理数[{}]。", ip, max);

            return false;
        }

        ipMap.put(ip, n);

        return true;
    }

    @Override
    public int get() {
        return counter.get();
    }

    @Override
    public void decrease(String ip) {
        counter.decrementAndGet();

        if (trustfulIp.contains(ip))
            return;

        int n = converter.toInt(ipMap.get(ip));
        if (n < 2)
            ipMap.remove(ip);
        else
            ipMap.put(ip, n - 1);
    }
}
