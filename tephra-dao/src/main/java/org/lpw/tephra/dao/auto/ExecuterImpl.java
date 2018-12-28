package org.lpw.tephra.dao.auto;

import org.lpw.tephra.bean.ContextRefreshedListener;
import org.lpw.tephra.crypto.Digest;
import org.lpw.tephra.dao.jdbc.DataSource;
import org.lpw.tephra.dao.jdbc.Sql;
import org.lpw.tephra.dao.jdbc.SqlTable;
import org.lpw.tephra.dao.model.Model;
import org.lpw.tephra.dao.model.ModelTable;
import org.lpw.tephra.dao.model.ModelTables;
import org.lpw.tephra.dao.orm.lite.LiteOrm;
import org.lpw.tephra.dao.orm.lite.LiteQuery;
import org.lpw.tephra.scheduler.DateJob;
import org.lpw.tephra.util.DateTime;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import javax.persistence.Table;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author lpw
 */
@Repository(AutoModel.NAME + ".executer")
public class ExecuterImpl implements Executer, ContextRefreshedListener, DateJob {
    @Inject
    private Digest digest;
    @Inject
    private DateTime dateTime;
    @Inject
    private DataSource dataSource;
    @Inject
    private Sql sql;
    @Inject
    private ModelTables modelTables;
    @Inject
    private LiteOrm liteOrm;
    @Inject
    private Create create;
    @Inject
    private Memory memory;
    @Inject
    private Update update;
    @Inject
    private Daily daily;
    private Map<String, Set<String>> map;
    private boolean hasAutoTable;
    private String autoTableName = AutoModel.class.getAnnotation(Table.class).name();

    @Override
    public int execute(String dataSource, String sql, boolean state0) {
        try {
            sql = sql.trim();
            if (sql.equals("") || sql.charAt(0) == '-')
                return 0;

            if (hasAutoTable()) {
                String md5 = digest.md5(dataSource + sql);
                if (state0 && liteOrm.findOne(new LiteQuery(AutoModel.class).where("c_md5=? and c_state=?"), new Object[]{md5, 0}) != null) {
                    liteOrm.close();

                    return 0;
                }

                AutoModel auto = new AutoModel();
                auto.setMd5(md5);
                auto.setDataSource(dataSource);
                auto.setSql(sql);
                auto.setTime(dateTime.now());
                liteOrm.save(auto);
            } else if (sql.contains("CREATE TABLE " + autoTableName))
                map.get(dataSource).add(autoTableName);

            int n = this.sql.update(dataSource, sql, new Object[0]);
            this.sql.close();

            return n;
        } catch (Throwable throwable) {
            this.sql.fail(throwable);

            return 0;
        }
    }

    private boolean hasAutoTable() {
        if (hasAutoTable)
            return true;

        return hasAutoTable = map.get(dataSource.getDefaultKey()).contains(autoTableName);
    }

    @Override
    public void create(Class<? extends Model> modelClass, String tableName) {
        ModelTable modelTable = modelTables.get(modelClass);
        String dataSource = this.dataSource.getKey(modelTable.getDataSource());
        if (map.get(dataSource).contains(tableName))
            return;

        create.create(dataSource, modelTable, tableName);
        map.get(dataSource).add(tableName);
    }

    @Override
    public Set<String> tables(String dataSource) {
        return new HashSet<>(map.get(dataSource));
    }

    @Override
    public int getContextRefreshedSort() {
        return 4;
    }

    @Override
    public void onContextRefreshed() {
        map = new HashMap<>();
        dataSource.getDialects().forEach((key, dialect) -> {
            Set<String> set = new HashSet<>();
            SqlTable sqlTable = sql.query(key, dialect.selectTables(dataSource.getConfig(key).getString("schema")), null);
            for (int i = 0; i < sqlTable.getRowCount(); i++)
                set.add(sqlTable.get(i, 0));
            sql.close();
            map.put(key, set);
        });

        create.execute(map);
        memory.execute();
        update.execute();
        daily.execute();
    }

    @Override
    public void executeDateJob() {
        daily.execute();
    }
}
