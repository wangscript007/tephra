package org.lpw.tephra.dao.auto;

import org.lpw.tephra.dao.jdbc.Sql;
import org.lpw.tephra.dao.jdbc.SqlTable;
import org.lpw.tephra.dao.model.ModelTable;
import org.lpw.tephra.dao.model.ModelTables;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Numeric;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

/**
 * @author lpw
 */
@Repository(AutoModel.NAME + ".memory")
public class MemoryImpl implements Memory {
    @Inject
    private Numeric numeric;
    @Inject
    private Logger logger;
    @Inject
    private Sql sql;
    @Inject
    private ModelTables modelTables;
    @Inject
    private Executer executer;

    @Override
    public void execute() {
        modelTables.getModelClasses().forEach(modelClass -> memory(modelTables.get(modelClass)));
    }

    private void memory(ModelTable modelTable) {
        if (modelTable.getMemoryName() == null || count(modelTable.getMemoryName()) == count(modelTable.getTableName()))
            return;

        int delete = executer.execute(null, "DELETE FROM " + modelTable.getMemoryName(), false);
        int insert = executer.execute(null, "INSERT INTO " + modelTable.getMemoryName() + " SELECT * FROM " + modelTable.getTableName(), false);
        if (logger.isInfoEnable())
            logger.info("同步内存表[{}:{}]数据[{}:{}]。", modelTable.getMemoryName(), delete, modelTable.getTableName(), insert);
    }

    private int count(String tableName) {
        SqlTable sqlTable = sql.query("SELECT COUNT(*) FROM " + tableName, null);

        return numeric.toInt(sqlTable.get(0, 0));
    }
}
