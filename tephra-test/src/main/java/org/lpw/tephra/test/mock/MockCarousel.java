package org.lpw.tephra.test.mock;

/**
 * @author lpw
 */
public interface MockCarousel {
    /**
     * 注册服务结果。
     *
     * @param key    服务key。
     * @param result 服务执行结果。
     */
    void register(String key, String result);
}
