package org.lpw.tephra.workbench.crud;

import org.lpw.tephra.dao.model.Model;
import org.lpw.tephra.dao.orm.PageList;
import org.lpw.tephra.dao.orm.hibernate.HibernateOrm;
import org.lpw.tephra.dao.orm.hibernate.HibernateQuery;
import org.lpw.tephra.util.Validator;
import org.lpw.tephra.workbench.Suffix;
import org.lpw.tephra.workbench.ui.SearchType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lpw
 */
@Repository("tephra.workbench.crud" + Suffix.DAO)
public class CrudDaoImpl implements CrudDao {
    @Autowired
    protected Validator validator;
    @Autowired
    protected HibernateOrm hibernateOrm;

    @Override
    public <T extends Model> PageList<T> query(Class<T> modelClass, List<Object[]> args, int pageSize, int pageNumber) {
        if (validator.isEmpty(args))
            return hibernateOrm.query(new HibernateQuery(modelClass).size(pageSize).page(pageNumber), new Object[0]);

        StringBuilder where = new StringBuilder();
        List<Object> list = new ArrayList<>();
        for (Object[] objects : args) {
            if (where.length() > 0)
                where.append(" and ");
            SearchType type = (SearchType) objects[1];
            where.append(objects[0]).append(type.getType());
            if (type == SearchType.Like)
                list.add("%" + objects[2] + "%");
            else
                for (int i = 2; i < objects.length; i++)
                    list.add(objects[i]);
        }

        return hibernateOrm.query(new HibernateQuery(modelClass).where(where.toString()).size(pageSize).page(pageNumber), list.toArray());
    }

    @Override
    public <T extends Model> T findById(Class<T> modelClass, String id) {
        return hibernateOrm.findById(modelClass, id);
    }

    @Override
    public <T extends Model> void save(T model) {
        hibernateOrm.save(model);
    }
}
