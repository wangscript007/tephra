package org.lpw.tephra.dao.auto;

import org.lpw.tephra.dao.jdbc.DataSource;
import org.lpw.tephra.dao.model.Model;
import org.lpw.tephra.dao.model.ModelTable;
import org.lpw.tephra.dao.model.ModelTables;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.sql.Date;
import java.util.Calendar;
import java.util.Map;
import java.util.Set;

/**
 * @author lpw
 */
@Repository(AutoModel.NAME + ".daily")
public class DailyImpl implements Daily {
    @Inject
    private ModelTables modelTables;
    @Inject
    private DataSource dataSource;
    @Inject
    private Executer executer;
    @Inject
    private Create create;

    @Override
    public void execute(Map<String, Set<String>> tables) {
        modelTables.getModelClasses().forEach(modelClass -> create(tables, modelTables.get(modelClass), modelClass));
    }

    private void create(Map<String, Set<String>> tables, ModelTable modelTable, Class<? extends Model> modelClass) {
        String dataSource = this.dataSource.getKey(modelTable.getDataSource());
        if (tables.containsKey(dataSource) && tables.get(dataSource).contains(modelTable.getTableName()))
            return;

        String[] array = create.read(modelClass);
        if (array == null)
            return;

        String tableName = modelTable.getTableName(null);
        Calendar calendar = Calendar.getInstance();
        for (String string : array) {
            for (int i = 0; i < 3; i++) {
                calendar.add(Calendar.DAY_OF_MONTH, i);
                String str = string.replaceFirst(tableName, modelTable.getTableName(new Date(calendar.getTimeInMillis())));
                executer.execute(dataSource, str, false);
            }
        }
    }
}
