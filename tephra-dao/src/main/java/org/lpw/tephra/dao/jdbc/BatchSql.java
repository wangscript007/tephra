package org.lpw.tephra.dao.jdbc;

/**
 * 批量SQL执行器。
 * 可将当前线程所有UPDATE SQL搜集起来在一个事务中提交。
 * 主要用于长事务，并且对原子性要求比较严格的场景。
 *
 * @author lpw
 */
public interface BatchSql {
    /**
     * 开始收集UPDATE SQL。
     */
    void begin();

    /**
     * 收集UPDATE SQL。
     *
     * @param dataSource 数据源。
     * @param sql        SQL。
     * @param args       参数集。
     * @return 是否被收集，已收集则返回true；否则返回false。
     */
    boolean collect(String dataSource, String sql, Object[] args);

    /**
     * 提交事务。
     */
    void commit();
}
