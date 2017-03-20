package org.lpw.tephra.atomic;

/**
 * @author lpw
 */
public interface Closables {
    /**
     * 关闭所有可关闭事务。
     */
    void close();
}
