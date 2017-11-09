package org.lpw.tephra.dao.jdbc;

import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lpw
 */
@Repository("tephra.dao.jdbc.batch-sql")
public class BatchSqlImpl implements BatchSql {
    @Inject
    private Sql sql;
    private ThreadLocal<List<String>> tlDataSource = new ThreadLocal<>();
    private ThreadLocal<List<String>> tlSql = new ThreadLocal<>();
    private ThreadLocal<List<Object[]>> tlArgs = new ThreadLocal<>();

    @Override
    public void begin() {
        if (tlSql.get() != null)
            return;

        tlDataSource.set(new ArrayList<>());
        tlSql.set(new ArrayList<>());
        tlArgs.set(new ArrayList<>());
    }

    @Override
    public boolean collect(String dataSource, String sql, Object[] args) {
        if (tlSql.get() == null)
            return false;

        tlDataSource.get().add(dataSource);
        tlSql.get().add(sql);
        tlArgs.get().add(args);

        return true;
    }

    @Override
    public void commit() {
        if (tlSql.get() == null)
            return;

        List<String> lDataSource = tlDataSource.get();
        tlDataSource.remove();
        List<String> lSql = tlSql.get();
        tlSql.remove();
        List<Object[]> lArgs = tlArgs.get();
        tlArgs.remove();

        if (lSql.isEmpty())
            return;

        for (int i = 0, size = lSql.size(); i < size; i++)
            sql.update(lDataSource.get(i), lSql.get(i), lArgs.get(i));
        sql.close();
    }
}
