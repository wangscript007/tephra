package org.lpw.tephra.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author lpw
 */
@Component("tephra.scheduler.minute")
public class MinuteSchedulerImpl extends SchedulerSupport<MinuteJob> implements MinuteScheduler {
    @Autowired(required = false)
    protected Set<MinuteJob> jobs;

    @Scheduled(cron = "${tephra.scheduler.minute.cron:0 * * * * ?}")
    @Override
    public synchronized void execute() {
        if (validator.isEmpty(jobs))
            return;

        if (logger.isDebugEnable())
            logger.debug("开始执行每分钟定时器调度。。。");

        jobs.forEach(this::pool);

        if (logger.isDebugEnable())
            logger.debug("成功执行{}个每分钟定时器任务！", jobs.size());
    }

    @Override
    protected void execute(MinuteJob job) {
        job.executeMinuteJob();
    }
}
