package org.lpw.tephra.dao.auto;

import org.lpw.tephra.util.Context;
import org.lpw.tephra.util.Converter;
import org.lpw.tephra.util.Io;
import org.lpw.tephra.util.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

/**
 * @author lpw
 */
@Repository(AutoModel.NAME + "update")
public class UpdateImpl implements Update {
    @Inject
    private Validator validator;
    @Inject
    private Context context;
    @Inject
    private Io io;
    @Inject
    private Converter converter;
    @Inject
    private Executer executer;
    @Value("${" + AutoModel.NAME + ".update:/WEB-INF/update.sql}")
    private String update;

    @Override
    public void execute() {
        if (validator.isEmpty(update))
            return;

        String[] sqls = converter.toArray(io.readAsString(context.getAbsolutePath(update)), ";");
        if (validator.isEmpty(sqls))
            return;

        for (String sql : sqls)
            executer.execute(null, sql, true);
    }
}
