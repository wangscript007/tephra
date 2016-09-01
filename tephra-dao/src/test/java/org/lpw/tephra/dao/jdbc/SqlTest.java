package org.lpw.tephra.dao.jdbc;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lpw.tephra.dao.DaoUtil;
import org.lpw.tephra.util.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author lpw
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:**/spring.xml"})
public class SqlTest {
    @Autowired
    protected Converter converter;
    @Autowired
    protected Sql sql;

    @Test
    public void crud() {
        DaoUtil.createTable(null);
        SqlTable table = sql.query("select * from t_tephra_test", null);
        Assert.assertNotNull(table);
        Assert.assertEquals(0, table.getRowCount());
        Assert.assertEquals(3, table.getColumnCount());

        for (int i = 0; i < 9; i++)
            sql.update("insert into t_tephra_test values(?,?,?);", new Object[]{"id" + i, i, "name" + i});
        table = sql.query("select * from t_tephra_test order by c_sort", null);
        Assert.assertEquals(9, table.getRowCount());
        Assert.assertEquals(3, table.getColumnCount());
        check(table, 0, 0);

        sql.update("update t_tephra_test set c_name=? where c_id=?;", new Object[]{"tephra", "id0"});
        table = sql.query("select * from t_tephra_test order by c_sort", null);
        Assert.assertEquals(9, table.getRowCount());
        Assert.assertEquals(3, table.getColumnCount());
        Assert.assertEquals("id0", table.get(0, 0));
        Assert.assertEquals(0, converter.toInt(table.get(0, 1)));
        Assert.assertEquals("tephra", table.get(0, 2));
        Assert.assertEquals("id0", table.get(0, "c_id"));
        Assert.assertEquals(0, converter.toInt(table.get(0, "c_sort")));
        Assert.assertEquals("tephra", table.get(0, "c_name"));
        check(table, 1, 0);

        sql.update("delete from t_tephra_test where c_id=?;", new Object[]{"id0"});
        table = sql.query("select * from t_tephra_test order by c_sort", null);
        Assert.assertEquals(8, table.getRowCount());
        Assert.assertEquals(3, table.getColumnCount());
        check(table, 1, 1);

        sql.update("delete from t_tephra_test;", new Object[0]);
        table = sql.query("select * from t_tephra_test order by c_sort", null);
        Assert.assertEquals(0, table.getRowCount());
        Assert.assertEquals(3, table.getColumnCount());

        sql.close();
    }

    protected void check(SqlTable table, int start, int off) {
        for (int i = start; i < 9 - off; i++) {
            Assert.assertEquals("id" + (i + off), table.get(i, 0));
            Assert.assertEquals(i + off, converter.toInt(table.get(i, 1)));
            Assert.assertEquals("name" + (i + off), table.get(i, 2));
            Assert.assertEquals("id" + (i + off), table.get(i, "c_id"));
            Assert.assertEquals(i + off, converter.toInt(table.get(i, "c_sort")));
            Assert.assertEquals("name" + (i + off), table.get(i, "c_name"));
        }
    }
}
