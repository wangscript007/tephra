package org.lpw.tephra.dao.dialect;

/**
 * 数据库方言。
 *
 * @author lpw
 */
public interface Dialect {
    /**
     * 获取方言名称。
     *
     * @return 方言名称。
     */
    String getName();

    /**
     * 获取驱动类名称。
     *
     * @return 驱动类名称。
     */
    String getDriver();

    /**
     * 获取访问URL地址。
     *
     * @param ip     IP地址。
     * @param schema 数据库名称。
     * @return 访问URL地址。
     */
    String getUrl(String ip, String schema);

    /**
     * 获取验证SQL语句。
     *
     * @return 验证SQL语句。
     */
    String getValidationQuery();

    /**
     * 获取Hibernate使用的数据库方言类名称。
     *
     * @return Hibernate使用的数据库方言类名称。
     */
    String getHibernateDialect();

    /**
     * 添加分页设置。
     *
     * @param sql  SQL语句。
     * @param size 每页显示记录数。
     * @param page 当前显示页码值。
     */
    void appendPagination(StringBuilder sql, int size, int page);
}
