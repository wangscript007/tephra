package org.lpw.tephra.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author lpw
 */
@Component("tephra.scheduler.hour")
public class HourSchedulerImpl extends SchedulerSupport<HourJob> implements HourScheduler {
    @Autowired(required = false)
    protected Set<HourJob> jobs;

    @Scheduled(cron = "${tephra.scheduler.hour.cron:30 0 * * * ?}")
    @Override
    public synchronized void execute() {
        if (validator.isEmpty(jobs))
            return;

        if (logger.isDebugEnable())
            logger.debug("开始执行每小时定时器调度。。。");

        jobs.forEach(this::pool);

        if (logger.isDebugEnable())
            logger.debug("成功执行{}个每小时定时器任务！", jobs.size());
    }

    @Override
    protected void execute(HourJob job) {
        job.executeHourJob();
    }
}
