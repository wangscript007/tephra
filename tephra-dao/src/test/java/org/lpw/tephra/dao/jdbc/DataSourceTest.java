package org.lpw.tephra.dao.jdbc;

import org.junit.Assert;
import org.junit.Test;
import org.lpw.tephra.dao.DaoUtil;
import org.lpw.tephra.dao.dialect.Dialect;
import org.lpw.tephra.test.DaoTestSupport;
import org.lpw.tephra.util.Generator;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author lpw
 */
public class DataSourceTest extends DaoTestSupport {
    @Inject
    private Generator generator;
    @Inject
    private DataSource dataSource;

    @Test
    public void config() throws Exception {
        javax.sql.DataSource ds = dataSource.getWriteable("");
        Assert.assertNotNull(ds);
        Assert.assertEquals(ds.hashCode(), dataSource.getReadonly("").hashCode());
        Assert.assertNull(dataSource.listReadonly(""));

        Map<String, Dialect> map = dataSource.getDialects();
        Assert.assertEquals("mysql", map.get("").getName());
        Assert.assertEquals("mysql", dataSource.getDialect(null).getName());
        Assert.assertEquals("mysql", dataSource.getDialect("").getName());
        int size = map.size();

        Field field = DataSourceImpl.class.getDeclaredField("config");
        field.setAccessible(true);
        Object object = field.get(dataSource);
        field.set(dataSource, "");
        ((DataSourceImpl) dataSource).onContextRefreshed();
        Assert.assertEquals(size, dataSource.getDialects().size());

        field.set(dataSource, object);
    }

    @Test
    public void create() {
        String name = generator.chars(8);
        Assert.assertNull(dataSource.getWriteable(name));

        DaoUtil.createDataSource(name, new String[]{"127.0.0.1:3306", "localhost:3306", "localhost:3306"});
        javax.sql.DataSource writeable = dataSource.getWriteable(name);
        Assert.assertNotNull(writeable);
        javax.sql.DataSource readonly = dataSource.getReadonly(name);
        Assert.assertNotNull(readonly);
        Assert.assertNotEquals(writeable.hashCode(), readonly.hashCode());
        List<javax.sql.DataSource> list = dataSource.listReadonly(name);
        Assert.assertEquals(2, list.size());
        Set<Integer> set = new HashSet<>();
        for (int i = 0; i < 100; i++)
            set.add(dataSource.getReadonly(name).hashCode());
        Assert.assertEquals(2, set.size());

        Map<String, Dialect> map = dataSource.getDialects();
        Assert.assertEquals("mysql", map.get(name).getName());
        Assert.assertEquals("mysql", dataSource.getDialect(name).getName());
    }
}
