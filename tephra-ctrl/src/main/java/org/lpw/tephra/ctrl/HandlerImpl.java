package org.lpw.tephra.ctrl;

import org.lpw.tephra.scheduler.MinuteJob;
import org.lpw.tephra.util.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author lpw
 */
@Controller("tephra.ctrl.handler")
public class HandlerImpl implements Handler, MinuteJob {
    @Value("${tephra.ctrl.handler.queue:true}")
    private boolean queue;
    @Value("${tephra.ctrl.handler.max-idle:30}")
    private int maxIdle;
    private Map<String, ExecutorService> executors = new ConcurrentHashMap<>();
    private Map<String, List<Future<?>>> futures = new ConcurrentHashMap<>();
    private Map<String, Long> times = new ConcurrentHashMap<>();

    @Override
    public <T> T call(String key, Callable<T> callable) throws Exception {
        return queue ? submit(key, callable).get() : callable.call();
    }

    @Override
    public <T> Future<T> submit(String key, Callable<T> callable) {
        Future<T> future = getExecutorService(key).submit(callable);
        addFuture(key, future);

        return future;
    }

    @Override
    public void run(String key, Runnable runnable) {
        if (queue)
            addFuture(key, getExecutorService(key).submit(runnable));
        else
            runnable.run();
    }

    private ExecutorService getExecutorService(String key) {
        return executors.computeIfAbsent(key, k -> Executors.newSingleThreadExecutor());
    }

    private void addFuture(String key, Future<?> future) {
        futures.computeIfAbsent(key, k -> Collections.synchronizedList(new ArrayList<>())).add(future);
    }

    @Override
    public void clear(String key) {
        if (executors.containsKey(key))
            executors.remove(key).shutdown();
        if (futures.containsKey(key))
            futures.remove(key);
        if (times.containsKey(key))
            times.remove(key);
    }

    @Override
    public void executeMinuteJob() {
        if (!queue)
            return;

        Set<String> set = new HashSet<>();
        futures.forEach((key, list) -> {
            if (list.isEmpty()) {
                if (System.currentTimeMillis() - times.getOrDefault(key, 0L) > maxIdle * TimeUnit.Minute.getTime())
                    set.add(key);

                return;
            }

            Set<Future<?>> dones = new HashSet<>();
            list.stream().filter(Future::isDone).forEach(dones::add);
            if (dones.isEmpty())
                return;

            list.removeAll(dones);
            if (list.isEmpty())
                times.put(key, System.currentTimeMillis());
        });
        if (!set.isEmpty())
            set.forEach(this::clear);
    }
}
