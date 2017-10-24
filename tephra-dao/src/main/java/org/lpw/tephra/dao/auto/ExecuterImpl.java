package org.lpw.tephra.dao.auto;

import org.lpw.tephra.bean.ContextRefreshedListener;
import org.lpw.tephra.crypto.Digest;
import org.lpw.tephra.dao.jdbc.Sql;
import org.lpw.tephra.dao.orm.lite.LiteOrm;
import org.lpw.tephra.dao.orm.lite.LiteQuery;
import org.lpw.tephra.util.DateTime;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

/**
 * @author lpw
 */
@Repository(AutoModel.NAME + ".executer")
public class ExecuterImpl implements Executer, ContextRefreshedListener {
    @Inject
    private Digest digest;
    @Inject
    private DateTime dateTime;
    @Inject
    private Sql sql;
    @Inject
    private LiteOrm liteOrm;
    @Inject
    private Create create;
    @Inject
    private Memory memory;
    @Inject
    private Update update;

    @Override
    public int execute(String dataSource, String sql, boolean state0) {
        String md5 = digest.md5(dataSource + sql);
        if (state0 && liteOrm.findOne(new LiteQuery(AutoModel.class).where("c_md5=? and c_state=?"), new Object[]{md5, 0}) != null)
            return 0;

        AutoModel auto = new AutoModel();
        auto.setMd5(md5);
        auto.setDataSource(dataSource);
        auto.setSql(sql);
        auto.setTime(dateTime.now());
        liteOrm.save(auto);

        return this.sql.update(dataSource, sql, new Object[0]);
    }

    @Override
    public int getContextRefreshedSort() {
        return 4;
    }

    @Override
    public void onContextRefreshed() {
        create.execute();
        memory.execute();
        update.execute();
    }
}
