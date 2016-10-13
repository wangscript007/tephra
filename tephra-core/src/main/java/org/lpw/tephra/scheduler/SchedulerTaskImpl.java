package org.lpw.tephra.scheduler;

import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.TimerTask;

/**
 * @auth lpw
 */
@Component("tephra.scheduler.task")
public class SchedulerTaskImpl extends TimerTask implements SchedulerTask {
    @Autowired
    protected Validator validator;
    @Autowired
    protected Logger logger;
    @Autowired(required = false)
    protected Set<SchedulerJobListener> listeners;
    protected SchedulerJob job;

    @Override
    public TimerTask setJob(SchedulerJob job) {
        this.job = job;

        return this;
    }

    @Override
    public void run() {
        if (!validator.isEmpty(listeners))
            listeners.forEach(SchedulerJobListener::begin);

        try {
            if (logger.isDebugEnable())
                logger.debug("开始执行定时任务[{}]。", job.getSchedulerName());

            job.executeSchedulerJob();

            if (logger.isDebugEnable())
                logger.debug("定时任务[{}]执行完成。", job.getSchedulerName());
        } catch (Throwable e) {
            if (!validator.isEmpty(listeners))
                listeners.forEach(listener -> listener.exception(e));

            logger.warn(e, "执行定时任务[{}]时发生异常！", job.getSchedulerName());
        }

        if (!validator.isEmpty(listeners))
            listeners.forEach(SchedulerJobListener::finish);
    }
}
