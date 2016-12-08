package org.lpw.tephra.test.mock;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lpw
 */
@Component("tephra.test.mock.carousel")
public class MockCarouselImpl implements MockCarousel {
    protected Map<String, String> map = new HashMap<>();

    @Override
    public void register(String key, String result) {
        map.put(key, result);
    }

    @Around("target(org.lpw.tephra.carousel.CarouselHelper)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        if (point.getSignature().getName().equals("service")) {
            String key = (String) point.getArgs()[0];
            if (map.containsKey(key))
                return map.get(key);
        }

        return point.proceed();
    }
}
