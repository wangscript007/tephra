package org.lpw.tephra.ctrl.execute;

/**
 * 执行映射表。
 *
 * @author lpw
 */
public interface ExecuteMap {
    /**
     * 获取执行器。
     *
     * @param service 服务名称。
     * @return 执行器。如果不存在则返回null。
     */
    Executor get(String service);
}
