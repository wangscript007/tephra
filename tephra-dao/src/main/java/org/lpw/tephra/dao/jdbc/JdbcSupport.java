package org.lpw.tephra.dao.jdbc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.tephra.bean.BeanFactory;
import org.lpw.tephra.dao.Mode;
import org.lpw.tephra.util.Converter;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Validator;

import javax.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lpw
 */
abstract class JdbcSupport<T extends PreparedStatement> implements Jdbc {
    @Inject
    Validator validator;
    @Inject
    Converter converter;
    @Inject
    Logger logger;
    @Inject
    private DataSource dataSource;
    @Inject
    private Connection connection;
    @Inject
    private BatchUpdate batchUpdate;

    @Override
    public SqlTable query(String sql, Object[] args) {
        return query(null, sql, args);
    }

    @Override
    public JSONArray queryAsJson(String sql, Object[] args) {
        return queryAsJson(null, sql, args);
    }

    SqlTable query(ResultSet rs) throws SQLException {
        SqlTable sqlTable = BeanFactory.getBean(SqlTable.class);
        sqlTable.set(rs);
        rs.close();

        return sqlTable;
    }

    JSONArray queryAsJson(ResultSet rs) throws SQLException {
        JSONArray array = new JSONArray();
        String[] columns = new String[rs.getMetaData().getColumnCount()];
        for (int i = 0; i < columns.length; i++)
            columns[i] = rs.getMetaData().getColumnLabel(i + 1);
        for (; rs.next(); ) {
            JSONObject object = new JSONObject();
            for (String column : columns)
                object.put(column, converter.toString(rs.getObject(column)));
            array.add(object);
        }
        rs.close();

        return array;
    }

    @Override
    public int update(String sql, Object[] args) {
        return update((String) null, sql, args);
    }

    @Override
    public int update(String dataSource, String sql, Object[] args) {
        if (batchUpdate.collect(dataSource, sql, args))
            return -1;

        try {
            long time = System.currentTimeMillis();
            int n = update(getConnection(dataSource, Mode.Write), sql, args);

            if (logger.isDebugEnable())
                logger.debug("执行SQL[{}:{}:{}:{}]更新操作。", dataSource, sql, converter.toString(args),
                        System.currentTimeMillis() - time);

            return n;
        } catch (SQLException e) {
            logger.warn(e, "执行SQL[{}:{}:{}]更新时发生异常！", dataSource, sql, converter.toString(args));

            throw new RuntimeException(e);
        }
    }

    @Override
    public int[] update(List<String> dataSources, List<String> sqls, List<Object[]> args) {
        if (validator.isEmpty(dataSources) || validator.isEmpty(sqls) || validator.isEmpty(args)
                || dataSources.size() != sqls.size() || sqls.size() != args.size())
            return null;

        Map<String, java.sql.Connection> connections = new HashMap<>();
        Map<String, Savepoint> savepoints = new HashMap<>();
        try {
            long time = System.currentTimeMillis();
            int[] ns = new int[dataSources.size()];
            for (int i = 0; i < ns.length; i++) {
                String dataSource = this.dataSource.getKey(dataSources.get(i));
                java.sql.Connection connection = connections.get(dataSource);
                if (connection == null) {
                    connection = getConnection(dataSource, Mode.Write);
                    connections.put(dataSource, connection);
                    savepoints.put(dataSource, connection.setSavepoint());
                }
                ns[i] = update(connection, sqls.get(i), args.get(i));
            }
            for (String key : connections.keySet())
                connections.get(key).commit();
            for (String key : connections.keySet())
                connections.get(key).releaseSavepoint(savepoints.get(key));

            if (logger.isDebugEnable())
                logger.debug("执行SQL[{}:{}:{}:{}]更新操作。", converter.toString(dataSources),
                        converter.toString(sqls), converter.toString(args), System.currentTimeMillis() - time);

            return ns;
        } catch (SQLException e) {
            rollback(connections, savepoints);

            logger.warn(e, "执行SQL[{}:{}:{}]更新时发生异常！", converter.toString(dataSources),
                    converter.toString(sqls), converter.toString(args));

            throw new RuntimeException(e);
        } finally {
            close(connections);
        }
    }

    private void rollback(Map<String, java.sql.Connection> connections, Map<String, Savepoint> savepoints) {
        if (connections.isEmpty() || savepoints.isEmpty())
            return;

        connections.forEach((key, connection) -> {
            try {
                connection.rollback(savepoints.get(key));
            } catch (SQLException e) {
                logger.warn(e, "回滚数据库连接[{}]时发生异常！", key);
            }
        });
    }

    private void close(Map<String, java.sql.Connection> connections) {
        if (connections.isEmpty())
            return;

        connections.forEach((key, connection) -> {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.warn(e, "关闭数据库连接[{}]时发生异常！", key);
            }
        });
    }

    private int update(java.sql.Connection connection, String sql, Object[] args) throws SQLException {
        T pstmt = newPreparedStatement(connection, sql);
        setArgs(pstmt, args);
        int n = pstmt.executeUpdate();
        pstmt.close();

        return n;
    }

    T newPreparedStatement(String dataSource, Mode mode, String sql) throws SQLException {
        return newPreparedStatement(getConnection(dataSource, mode), sql);
    }

    abstract T newPreparedStatement(java.sql.Connection connection, String sql) throws SQLException;

    private java.sql.Connection getConnection(String dataSource, Mode mode) throws SQLException {
        java.sql.Connection connection = this.connection.get(dataSource, mode);
        if (connection == null)
            throw new NullPointerException("无法获得数据库[" + mode + "]连接！");

        return connection;
    }

    /**
     * 设置参数集。
     *
     * @param pstmt PreparedStatement实例。
     * @param args  参数集。
     * @throws SQLException 未处理SQLException异常。
     */
    void setArgs(T pstmt, Object[] args) throws SQLException {
        if (validator.isEmpty(args))
            return;

        for (int i = 0; i < args.length; i++)
            pstmt.setObject(i + 1, args[i]);
    }

    @Override
    public void fail(Throwable throwable) {
        connection.fail(throwable);
    }

    @Override
    public void close() {
        connection.close();
    }
}
