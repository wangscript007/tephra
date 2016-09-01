package org.lpw.tephra.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author lpw
 */
@Component("tephra.scheduler.date")
public class DateSchedulerImpl extends SchedulerSupport<DateJob> implements DateScheduler {
    @Autowired(required = false)
    protected Set<DateJob> jobs;

    @Scheduled(cron = "${tephra.scheduler.date.cron:30 30 4 * * ?}")
    @Override
    public synchronized void execute() {
        if (validator.isEmpty(jobs))
            return;

        if (logger.isDebugEnable())
            logger.debug("开始执行每日定时器调度。。。");

        jobs.forEach(this::pool);

        if (logger.isDebugEnable())
            logger.debug("成功执行{}个每日定时器任务！", jobs.size());
    }

    @Override
    public void execute(DateJob job) {
        job.executeDateJob();
    }
}
