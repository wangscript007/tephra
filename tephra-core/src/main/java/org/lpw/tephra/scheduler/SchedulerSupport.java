package org.lpw.tephra.scheduler;

import org.lpw.tephra.bean.ContextClosedListener;
import org.lpw.tephra.bean.ContextRefreshedListener;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Validator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 定时器支持类。
 *
 * @author lpw
 */
public abstract class SchedulerSupport<T> implements ContextRefreshedListener, ContextClosedListener {
    @Autowired
    protected Validator validator;
    @Autowired
    protected Logger logger;
    @Autowired(required = false)
    protected Set<SchedulerJobListener> listeners;
    protected Set<Integer> runningJobs;
    protected ExecutorService executorService;

    /**
     * 判断指定Job是否正在运行。
     *
     * @param job 要验证的Job对象。
     * @return 如果正在运行则返回true；否则返回false。
     */
    protected boolean isRunning(T job) {
        return job != null && runningJobs.contains(job.hashCode());
    }

    /**
     * 将Job设置为正在运行。
     *
     * @param job 要设置的Job对象。
     */
    protected void begin(T job) {
        if (job != null)
            runningJobs.add(job.hashCode());

        if (!validator.isEmpty(listeners))
            listeners.forEach(SchedulerJobListener::begin);
    }

    /**
     * 添加到执行线程池中。
     *
     * @param job 要执行的任务实例。
     */
    protected void pool(T job) {
        if (isRunning(job))
            return;

        executorService.submit(() -> {
            begin(job);

            try {
                execute(job);
            } catch (Exception e) {
                exception(e);
            }

            finish(job);
        });
    }

    /**
     * 执行任务。
     *
     * @param job 要执行的任务实例。
     */
    protected abstract void execute(T job);

    /**
     * 处理执行异常。
     *
     * @param throwable 异常信息。
     */
    protected void exception(Throwable throwable) {
        if (!validator.isEmpty(listeners))
            listeners.forEach(listener -> listener.exception(throwable));

        logger.warn(throwable, "执行定时器任务时发生异常！");
    }

    /**
     * 设置Job对象运行结束。
     *
     * @param job 要设置的Job对象。
     */
    protected void finish(T job) {
        if (job != null)
            runningJobs.remove(job.hashCode());

        if (!validator.isEmpty(listeners))
            listeners.forEach(SchedulerJobListener::finish);
    }

    @Override
    public int getContextRefreshedSort() {
        return 6;
    }

    @Override
    public void onContextRefreshed() {
        runningJobs = Collections.synchronizedSet(new HashSet<>());
        executorService = Executors.newCachedThreadPool();
    }

    @Override
    public int getContextClosedSort() {
        return 6;
    }

    @Override
    public void onContextClosed() {
        executorService.shutdownNow();
    }
}
