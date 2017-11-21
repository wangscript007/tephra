package org.lpw.tephra.ctrl;

import org.lpw.tephra.scheduler.MinuteJob;
import org.lpw.tephra.util.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author lpw
 */
@Controller("tephra.ctrl.handler")
public class HandlerImpl implements Handler, MinuteJob {
    @Value("${tephra.ctrl.handler.queue:true}")
    private boolean queue;
    @Value("${tephra.ctrl.handler.max-idle:30}")
    private int maxIdle;
    private Map<String, ExecutorService> queueService = new ConcurrentHashMap<>();
    private Map<String, Long> queueTime = new ConcurrentHashMap<>();

    @Override
    public <T> T call(String key, Callable<T> callable) throws Exception {
        if (!queue)
            return callable.call();

        T t = queueService.computeIfAbsent(key, k -> Executors.newSingleThreadExecutor()).submit(callable).get();
        queueTime.put(key, System.currentTimeMillis());

        return t;
    }

    @Override
    public void run(String key, Runnable runnable) {
        if (queue)
            queueService.computeIfAbsent(key, k -> Executors.newSingleThreadExecutor()).submit(runnable);
        else
            runnable.run();
    }

    @Override
    public void executeMinuteJob() {
        if (!queue)
            return;

        Set<String> set = new HashSet<>();
        queueTime.forEach((key, time) -> {
            if (System.currentTimeMillis() - time > maxIdle * TimeUnit.Minute.getTime())
                set.add(key);
        });
        set.forEach(key -> {
            queueService.remove(key).shutdown();
            queueTime.remove(key);
        });
    }
}
