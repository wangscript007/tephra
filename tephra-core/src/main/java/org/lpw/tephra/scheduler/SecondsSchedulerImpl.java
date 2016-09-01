package org.lpw.tephra.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author lpw
 */
@Component("tephra.scheduler.seconds")
public class SecondsSchedulerImpl extends SchedulerSupport<SecondsJob> implements SecondsScheduler {
    @Autowired(required = false)
    protected Set<SecondsJob> jobs;

    @Scheduled(cron = "${tephra.scheduler.seconds.cron:* * * * * ?}")
    @Override
    public synchronized void execute() {
        if (validator.isEmpty(jobs))
            return;

        if (logger.isDebugEnable())
            logger.debug("开始执行每秒钟定时器调度。。。");

        jobs.forEach(this::pool);

        if (logger.isDebugEnable())
            logger.debug("成功执行{}个每秒钟定时器任务！", jobs.size());
    }

    @Override
    protected void execute(SecondsJob job) {
        job.executeSecondsJob();
    }
}
