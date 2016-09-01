package org.lpw.tephra.dao.jdbc;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.lpw.tephra.dao.orm.hibernate.Session;
import org.lpw.tephra.util.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author lpw
 */
@Aspect
@Repository("tephra.dao.jdbc.transactional")
public class Transactional {
    @Autowired
    protected Logger logger;
    @Autowired
    protected Connection connection;
    @Autowired
    protected Session session;

    @Around("execution(* *(..)) && @annotation(javax.transaction.Transactional)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        if (logger.isDebugEnable())
            logger.debug("开始[{}]事务控制。", point.getSignature().getName());

        connection.beginTransaction();
        session.beginTransaction();
        Object object = point.proceed();
        session.close();
        connection.close();

        if (logger.isDebugEnable())
            logger.debug("事务[{}]控制结束。", point.getSignature().getName());

        return object;
    }
}
