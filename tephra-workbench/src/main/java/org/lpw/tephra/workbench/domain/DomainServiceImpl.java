package org.lpw.tephra.workbench.domain;

import org.lpw.tephra.ctrl.context.Request;
import org.lpw.tephra.ctrl.context.Session;
import org.lpw.tephra.workbench.Suffix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author lpw
 */
@Service(DomainModel.NAME + Suffix.SERVICE)
public class DomainServiceImpl implements DomainService {
    @Autowired
    protected Request request;
    @Autowired
    protected Session session;
    @Autowired
    protected DomainDao domainDao;

    @Override
    public DomainModel get() {
        DomainModel domain = session.get("domain");
        if (domain == null) {
            domain = domainDao.findByKey(request.get("domain"));
            if (domain == null)
                domain = domainDao.findByKey("default");
            if (domain != null)
                session.set("domain", domain);
        }

        return domain;
    }
}
