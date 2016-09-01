package org.lpw.tephra.dao.orm.mybatis;

import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.lpw.tephra.dao.DaoUtil;
import org.lpw.tephra.dao.Mode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author lpw
 */
@Repository("tephra.dao.orm.mybatis.transactional.impl")
public class TransactionalImpl {
    @Autowired
    protected SessionFactory sessionFactory;
    @Autowired
    protected Session session;

    @javax.transaction.Transactional
    public void get() {
        session.beginTransaction();
        SqlSession session1 = session.get(null, Mode.Write);
        Assert.assertNotNull(session1);
        SqlSession session2 = session.get(null, Mode.Read);
        Assert.assertEquals(session1.hashCode(), session2.hashCode());

        DaoUtil.createSessionFactory("HasReadonlyTransactional", sessionFactory);
        SqlSession session3 = session.get("HasReadonlyTransactional", Mode.Write);
        Assert.assertNotNull(session3);
        SqlSession session4 = session.get("HasReadonlyTransactional", Mode.Read);
        Assert.assertEquals(session3.hashCode(), session4.hashCode());
    }
}
