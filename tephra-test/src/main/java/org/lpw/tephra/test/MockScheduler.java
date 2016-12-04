package org.lpw.tephra.test;

/**
 * @author lpw
 */
public interface MockScheduler {
    /**
     * 暂停定时器。
     */
    void pause();

    /**
     * 继续定时器。
     */
    void press();
}
