package org.lpw.tephra.dao.auto;

import org.lpw.tephra.dao.jdbc.DataSource;
import org.lpw.tephra.dao.model.Model;
import org.lpw.tephra.dao.model.ModelTable;
import org.lpw.tephra.dao.model.ModelTables;
import org.lpw.tephra.util.Io;
import org.lpw.tephra.util.Logger;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

/**
 * @author lpw
 */
@Repository(AutoModel.NAME + ".create")
public class CreateImpl implements Create {
    @Inject
    private Io io;
    @Inject
    private Logger logger;
    @Inject
    private ModelTables modelTables;
    @Inject
    private DataSource dataSource;
    @Inject
    private Executer executer;

    @Override
    public void execute(Map<String, Set<String>> tables) {
        modelTables.getModelClasses().forEach(modelClass -> create(tables, modelTables.get(modelClass), modelClass));
    }

    private void create(Map<String, Set<String>> tables, ModelTable modelTable, Class<? extends Model> modelClass) {
        String dataSource = this.dataSource.getKey(modelTable.getDataSource());
        if (tables.containsKey(dataSource) && tables.get(dataSource).contains(modelTable.getTableName()))
            return;

        String[] array = read(modelClass);
        if (array == null)
            return;

        for (String string : array) {
            string = string.trim();
            if (string.length() == 0 || string.charAt(0) == '-')
                continue;

            executer.execute(dataSource, string, false);
        }
    }

    private String[] read(Class<? extends Model> modelClass) {
        try {
            InputStream inputStream = modelClass.getResourceAsStream("create.sql");
            if (inputStream == null)
                return null;

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            io.copy(inputStream, outputStream);
            inputStream.close();
            outputStream.close();

            return outputStream.toString().split(";\n");
        } catch (IOException e) {
            logger.warn(e, "读取DDL文件[{}:create.sql]时发生异常！", modelClass);

            return null;
        }
    }
}
