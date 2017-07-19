package org.lpw.tephra.ctrl;

/**
 * 调度。用于对请求进行分发。
 *
 * @author lpw
 */
public interface Dispatcher {
    /**
     * 执行服务。
     */
    void execute();

    /**
     * 获取执行时长，单位：毫秒。
     *
     * @return 执行时长。
     */
    long getTime();
}
