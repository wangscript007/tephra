package org.lpw.tephra.test;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @auth lpw
 */
@Aspect
@Component("tephra.test.mock-scheduler")
public class MockSchedulerImpl implements MockScheduler {
    protected boolean paused;

    @Override
    public void pause() {
        paused = true;
    }

    @Override
    public void press() {
        paused = false;
    }

    @Around("target(org.lpw.tephra.scheduler.Scheduler)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        return paused ? null : point.proceed();
    }
}
