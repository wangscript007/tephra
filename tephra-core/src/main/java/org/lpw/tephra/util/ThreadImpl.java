package org.lpw.tephra.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author lpw
 */
@Component("tephra.util.thread")
public class ThreadImpl implements Thread {
    @Autowired
    protected Generator generator;
    @Autowired
    protected Logger logger;

    @Override
    public void sleep(int time, TimeUnit unit) {
        sleep(1L * time * unit.getTime());
    }

    @Override
    public void sleep(int min, int max, TimeUnit unit) {
        sleep(generator.random(min * unit.getTime(), max * unit.getTime()));
    }

    protected void sleep(long time) {
        try {
            java.lang.Thread.sleep(time);
        } catch (InterruptedException e) {
            logger.warn(e, "线程休眠[{}]时发生异常！", time);
        }
    }
}
