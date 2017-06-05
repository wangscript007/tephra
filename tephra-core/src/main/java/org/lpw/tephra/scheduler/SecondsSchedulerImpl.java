package org.lpw.tephra.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Optional;
import java.util.Set;

/**
 * @author lpw
 */
@Component("tephra.scheduler.seconds")
public class SecondsSchedulerImpl extends SchedulerSupport<SecondsJob> implements SecondsScheduler {
    @Inject
    private Optional<Set<SecondsJob>> jobs;

    @Scheduled(cron = "${tephra.scheduler.seconds.cron:* * * * * ?}")
    @Override
    public synchronized void execute() {
        jobs.ifPresent(set -> {
            if (logger.isDebugEnable())
                logger.debug("开始执行每秒钟定时器调度。。。");

            set.forEach(this::pool);

            if (logger.isDebugEnable())
                logger.debug("成功执行{}个每秒钟定时器任务！", set.size());
        });
    }

    @Override
    protected void execute(SecondsJob job) {
        job.executeSecondsJob();
    }
}
