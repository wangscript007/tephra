package org.lpw.tephra.dao.orm.hibernate;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lpw.tephra.dao.DaoUtil;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

/**
 * @author lpw
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:**/spring.xml"})
public class SessionFactoryTest {
    @Inject
    private SessionFactory sessionFactory;

    @Test
    public void config() {
        org.hibernate.SessionFactory writeable = this.sessionFactory.getWriteable("");
        Assert.assertNotNull(writeable);
        org.hibernate.SessionFactory readonly = this.sessionFactory.getReadonly("");
        Assert.assertEquals(writeable.hashCode(), readonly.hashCode());
    }

    @Test
    public void create() {
        Assert.assertNull(sessionFactory.getWriteable("new"));

        DaoUtil.createSessionFactory("new", sessionFactory);

        org.hibernate.SessionFactory writeable = this.sessionFactory.getWriteable("new");
        Assert.assertNotNull(writeable);
        org.hibernate.SessionFactory readonly = this.sessionFactory.getReadonly("new");
        Assert.assertNotEquals(writeable.hashCode(), readonly.hashCode());
    }
}
