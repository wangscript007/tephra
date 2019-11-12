package org.lpw.tephra.dao;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.lpw.tephra.util.Logger;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.Optional;
import java.util.Set;

/**
 * @author lpw
 */
@Aspect
@Repository("tephra.dao.transactional")
public class Transactional {
    @Inject
    private Logger logger;
    @Inject
    private Optional<Set<Connection>> connections;

    @Around("execution(* *(..)) && @annotation(javax.transaction.Transactional)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        if (logger.isDebugEnable())
            logger.debug("开始[{}]事务控制。", point.getSignature().getName());

        connections.ifPresent(set -> set.forEach(Connection::beginTransaction));
        Object object = point.proceed();
        connections.ifPresent(set -> set.forEach(Connection::close));

        if (logger.isDebugEnable())
            logger.debug("事务[{}]控制结束。", point.getSignature().getName());

        return object;
    }
}
