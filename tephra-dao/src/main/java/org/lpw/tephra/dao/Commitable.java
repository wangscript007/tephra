package org.lpw.tephra.dao;

/**
 * @author lpw
 */
public interface Commitable {
    /**
     * 回滚本次事务所有更新操作。
     */
    void rollback();

    /**
     * 提交持久化，并关闭当前线程的所有连接。
     */
    void close();
}
