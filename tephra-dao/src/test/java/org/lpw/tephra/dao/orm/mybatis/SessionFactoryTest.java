package org.lpw.tephra.dao.orm.mybatis;

import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lpw.tephra.dao.DaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author lpw
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:**/spring.xml"})
public class SessionFactoryTest {
    @Autowired
    protected SessionFactory sessionFactory;

    @Test
    public void config() {
        SqlSessionFactory writeable = this.sessionFactory.getWriteable("");
        Assert.assertNotNull(writeable);
        SqlSessionFactory readonly = this.sessionFactory.getReadonly("");
        Assert.assertEquals(writeable.hashCode(), readonly.hashCode());
    }

    @Test
    public void create() {
        Assert.assertNull(sessionFactory.getWriteable("new"));

        DaoUtil.createSessionFactory("new", sessionFactory);

        SqlSessionFactory writeable = this.sessionFactory.getWriteable("new");
        Assert.assertNotNull(writeable);
        SqlSessionFactory readonly = this.sessionFactory.getReadonly("new");
        Assert.assertNotEquals(writeable.hashCode(), readonly.hashCode());
    }
}
