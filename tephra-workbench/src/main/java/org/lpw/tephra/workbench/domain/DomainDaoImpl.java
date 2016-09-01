package org.lpw.tephra.workbench.domain;

import org.lpw.tephra.dao.model.ModelHelper;
import org.lpw.tephra.dao.orm.lite.LiteOrm;
import org.lpw.tephra.dao.orm.lite.LiteQuery;
import org.lpw.tephra.dao.orm.mybatis.MybatisOrm;
import org.lpw.tephra.workbench.Suffix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author lpw
 */
@Repository(DomainModel.NAME + Suffix.DAO)
public class DomainDaoImpl implements DomainDao {
    @Autowired
    protected LiteOrm liteOrm;
    @Autowired
    protected MybatisOrm mybatisOrm;
    @Autowired
    protected ModelHelper modelHelper;

    @Override
    public DomainModel findByKey(String key) {
        return liteOrm.findOne(new LiteQuery(DomainModel.class).where("c_key=?"), new Object[]{key});
    }
}
