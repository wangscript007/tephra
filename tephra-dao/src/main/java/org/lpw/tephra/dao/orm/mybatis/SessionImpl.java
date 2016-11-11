package org.lpw.tephra.dao.orm.mybatis;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.lpw.tephra.dao.ConnectionSupport;
import org.lpw.tephra.dao.Mode;
import org.lpw.tephra.dao.jdbc.DataSource;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lpw
 */
@Repository("tephra.dao.orm.mybatis.session")
public class SessionImpl extends ConnectionSupport<SqlSession> implements Session {
    @Autowired
    protected Validator validator;
    @Autowired
    protected Logger logger;
    @Autowired
    protected DataSource dataSource;
    @Autowired
    protected SessionFactory sessionFactory;
    protected ThreadLocal<Map<String, SqlSession>> sessions = new ThreadLocal<>();
    protected ThreadLocal<Boolean> transactional = new ThreadLocal<>();

    @Override
    public void beginTransaction() {
        transactional.set(true);
    }

    @Override
    public SqlSession get(String dataSource, Mode mode) {
        if (dataSource == null)
            dataSource = "";
        if ((transactional.get() != null && transactional.get()) || !this.dataSource.hasReadonly(dataSource))
            mode = Mode.Write;
        Map<String, SqlSession> sessions = this.sessions.get();
        if (sessions == null)
            sessions = new HashMap<>();
        String key = dataSource + mode.ordinal();
        SqlSession session = sessions.get(key);
        if (session != null)
            return session;

        SqlSessionFactory sessionFactory = null;
        if (mode == Mode.Read)
            sessionFactory = this.sessionFactory.getReadonly(dataSource);
        if (sessionFactory == null)
            sessionFactory = this.sessionFactory.getWriteable(dataSource);
        if (sessionFactory == null)
            throw new NullPointerException("无法获得[" + mode + "]MyBatis环境！");

        session = sessionFactory.openSession(mode == Mode.Read);
        sessions.put(key, session);
        this.sessions.set(sessions);

        return session;
    }

    @Override
    public void fail(Throwable throwable) {
        Map<String, SqlSession> sessions = this.sessions.get();
        if (validator.isEmpty(sessions))
            return;

        sessions.forEach((key, session) -> {
            session.rollback();
            session.close();
        });
        this.sessions.remove();
        transactional.remove();

        if (logger.isDebugEnable())
            logger.debug("回滚[{}]MyBatis Session！", sessions.size());
    }

    @Override
    public void close() {
        Map<String, SqlSession> sessions = this.sessions.get();
        if (validator.isEmpty(sessions))
            return;

        sessions.forEach((key, session) -> {
            session.commit();
            session.close();
        });
        this.sessions.remove();
        transactional.remove();

        if (logger.isDebugEnable())
            logger.debug("关闭[{}]MyBatis Session！", sessions.size());
    }
}
