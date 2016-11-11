package org.lpw.tephra.atomic;

/**
 * 可失败事务。
 *
 * @auth lpw
 */
public interface Failable {
    /**
     * 失败。
     *
     * @param throwable 异常。
     */
    void fail(Throwable throwable);
}
