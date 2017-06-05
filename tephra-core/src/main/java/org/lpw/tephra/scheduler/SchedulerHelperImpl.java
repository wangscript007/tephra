package org.lpw.tephra.scheduler;

import org.lpw.tephra.bean.BeanFactory;
import org.lpw.tephra.bean.ContextClosedListener;
import org.lpw.tephra.bean.ContextRefreshedListener;
import org.lpw.tephra.util.Thread;
import org.lpw.tephra.util.TimeUnit;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Date;
import java.util.Timer;

/**
 * @author lpw
 */
@Component("tephra.scheduler.helper")
public class SchedulerHelperImpl implements SchedulerHelper, ContextRefreshedListener, ContextClosedListener {
    @Inject
    private Thread thread;
    private Timer timer;

    @Override
    public void delay(SchedulerJob job, long time) {
        if (job == null)
            return;

        waitForTimer();

        timer.schedule(BeanFactory.getBean(SchedulerTask.class).setJob(job), Math.max(0L, time));
    }

    @Override
    public void at(SchedulerJob job, Date time) {
        if (job == null || time == null)
            return;

        waitForTimer();

        timer.schedule(BeanFactory.getBean(SchedulerTask.class).setJob(job), time);
    }

    private void waitForTimer() {
        while (timer == null)
            thread.sleep(100, TimeUnit.MilliSecond);
    }

    @Override
    public int getContextRefreshedSort() {
        return 99;
    }

    @Override
    public void onContextRefreshed() {
        if (timer == null)
            timer = new Timer();
    }

    @Override
    public int getContextClosedSort() {
        return 99;
    }

    @Override
    public void onContextClosed() {
        if (timer != null)
            timer.cancel();
    }
}
