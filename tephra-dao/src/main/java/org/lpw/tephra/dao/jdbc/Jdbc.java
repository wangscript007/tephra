package org.lpw.tephra.dao.jdbc;

import com.alibaba.fastjson.JSONArray;
import org.lpw.tephra.atomic.Atomicable;

import java.util.List;

/**
 * JDBC操作接口。
 *
 * @author lpw
 */
public interface Jdbc extends Atomicable {
    /**
     * 执行检索操作。
     *
     * @param sql  SQL。
     * @param args 参数集。
     * @return 数据集。
     */
    SqlTable query(String sql, Object[] args);

    /**
     * 执行检索操作。
     *
     * @param dataSource 数据源名称，为空则使用默认数据源。
     * @param sql        SQL。
     * @param args       参数集。
     * @return 数据集。
     */
    SqlTable query(String dataSource, String sql, Object[] args);

    /**
     * 执行检索操作，并将结果集以JSON数组格式返回。
     *
     * @param sql  SQL。
     * @param args 参数集。
     * @return 数据集。
     */
    JSONArray queryAsJson(String sql, Object[] args);

    /**
     * 执行检索操作，并将结果集以JSON数组格式返回。
     *
     * @param dataSource 数据源名称，为空则使用默认数据源。
     * @param sql        SQL。
     * @param args       参数集。
     * @return 数据集。
     */
    JSONArray queryAsJson(String dataSource, String sql, Object[] args);

    /**
     * 执行更新操作。
     *
     * @param sql  SQL。
     * @param args 参数集。
     * @return 影响记录数。
     */
    int update(String sql, Object[] args);

    /**
     * 执行更新操作。
     *
     * @param dataSource 数据源名称，为空则使用默认数据源。
     * @param sql        SQL。
     * @param args       参数集。
     * @return 影响记录数。
     */
    int update(String dataSource, String sql, Object[] args);

    /**
     * 执行批量更新操作。通过设置保存点来确保事务。
     * 执行完毕将自动提交事务；如果执行异常将自动回滚。
     *
     * @param dataSources 数据源名称集。
     * @param sqls        SQL集。
     * @param args        参数集。
     * @return 影响记录数。
     */
    int[] update(List<String> dataSources, List<String> sqls, List<Object[]> args);
}
