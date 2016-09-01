package org.lpw.tephra.workbench.crud;

import org.lpw.tephra.dao.model.Model;
import org.lpw.tephra.dao.orm.PageList;

import java.util.List;

/**
 * @author lpw
 */
public interface CrudDao {
    <T extends Model> PageList<T> query(Class<T> modelClass, List<Object[]> args, int pageSize, int pageNumber);

    <T extends Model> T findById(Class<T> modelClass, String id);

    <T extends Model> void save(T model);
}
