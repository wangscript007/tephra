package org.lpw.tephra.dao;

import org.lpw.tephra.scheduler.SchedulerJobListener;

/**
 * @author lpw
 */
public abstract class ConnectionSupport<T> implements Connection<T>, SchedulerJobListener {
    @Override
    public void begin() {
    }

    @Override
    public void exception(Throwable throwable) {
        rollback();
    }

    @Override
    public void finish() {
        close();
    }
}
