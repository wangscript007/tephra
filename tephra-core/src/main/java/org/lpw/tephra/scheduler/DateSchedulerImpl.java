package org.lpw.tephra.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Optional;
import java.util.Set;

/**
 * @author lpw
 */
@Component("tephra.scheduler.date")
public class DateSchedulerImpl extends SchedulerSupport<DateJob> implements DateScheduler {
    @Inject
    private Optional<Set<DateJob>> jobs;

    @Scheduled(cron = "${tephra.scheduler.date.cron:30 30 4 * * ?}")
    @Override
    public synchronized void execute() {
        jobs.ifPresent(set -> {
            if (logger.isDebugEnable())
                logger.debug("开始执行每日定时器调度。。。");

            set.forEach(this::pool);

            if (logger.isDebugEnable())
                logger.debug("成功执行{}个每日定时器任务！", set.size());
        });
    }

    @Override
    public void execute(DateJob job) {
        job.executeDateJob();
    }
}
