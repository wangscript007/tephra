package org.lpw.tephra.dao;

import net.sf.json.JSONObject;
import org.lpw.tephra.bean.BeanFactory;
import org.lpw.tephra.dao.jdbc.DataSource;

import java.sql.PreparedStatement;

/**
 * @author lpw
 */
public class DaoUtil {
    public static void createDataSource(String key) {
        JSONObject config = new JSONObject();
        config.put("key", key);
        config.put("type", "mysql");
        config.put("username", "root");
        config.put("password", "root");
        config.put("ips", new String[]{"127.0.0.1:3306", "localhost:3306"});
        config.put("schema", "d_tephra_test");
        BeanFactory.getBean(DataSource.class).create(config);
    }

    public static void createSessionFactory(String key, ConnectionFactory<?> sessionFactory) {
        createDataSource(key);

        JSONObject config = new JSONObject();
        config.put("key", key);
        config.put("values", new String[]{"org.lpw.tephra"});
        sessionFactory.create(config);
    }

    public static void createTable(String dataSource) {
        update(dataSource, "DROP TABLE IF EXISTS t_tephra_test;");
        update(dataSource, "CREATE TABLE t_tephra_test (" +
                "  c_id CHAR(36) NOT NULL COMMENT '主键'," +
                "  c_sort INT DEFAULT 0 COMMENT '顺序'," +
                "  c_name VARCHAR(255) NOT NULL COMMENT '名称'," +
                "  PRIMARY KEY pk_tephra_test(c_id)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8;");

        update(dataSource, "DROP TABLE IF EXISTS m_tephra_test;");
        update(dataSource, "CREATE TABLE m_tephra_test (" +
                "  c_id CHAR(36) NOT NULL COMMENT '主键'," +
                "  c_sort INT DEFAULT 0 COMMENT '顺序'," +
                "  c_name VARCHAR(255) NOT NULL COMMENT '名称'," +
                "  PRIMARY KEY pk_tephra_test(c_id)" +
                ") ENGINE=Memory DEFAULT CHARSET=utf8;");
    }

    public static void createMybatisTable(String dataSource) {
        update(dataSource, "DROP TABLE IF EXISTS t_tephra_mybatis;");
        update(dataSource, "CREATE TABLE t_tephra_mybatis (" +
                "  id CHAR(36) NOT NULL COMMENT '主键'," +
                "  sort INT DEFAULT 0 COMMENT '顺序'," +
                "  name VARCHAR(255) NOT NULL COMMENT '名称'," +
                "  PRIMARY KEY pk_tephra_mybatis(id)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
    }

    protected static void update(String dataSource, String sql) {
        try {
            java.sql.Connection connection = BeanFactory.getBean(DataSource.class).getWriteable(dataSource == null ? "" : dataSource).getConnection();
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.executeUpdate();
            pstmt.close();
            connection.close();
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void close() {
        BeanFactory.getBeans(Commitable.class).forEach(Commitable::close);
    }
}
