package org.lpw.tephra.dao.model;

import org.lpw.tephra.bean.ContextRefreshedListener;
import org.lpw.tephra.dao.jdbc.DataSource;
import org.lpw.tephra.dao.jdbc.Sql;
import org.lpw.tephra.dao.jdbc.SqlTable;
import org.lpw.tephra.util.Io;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Numeric;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * @author lpw
 */
@Repository("tephra.dao.model.auto-update")
public class AutoUpdateImpl implements ContextRefreshedListener {
    @Inject
    private Io io;
    @Inject
    private Numeric numeric;
    @Inject
    private Logger logger;
    @Inject
    private DataSource dataSource;
    @Inject
    private Sql sql;
    @Inject
    private ModelTables modelTables;

    @Override
    public int getContextRefreshedSort() {
        return 4;
    }

    @Override
    public void onContextRefreshed() {
        Set<String> tables = new HashSet<>();
        dataSource.getDialects().forEach((key, dialect) -> {
            SqlTable sqlTable = sql.query(key, dialect.selectTables(dataSource.getConfig(key).getString("schema")), null);
            for (int i = 0; i < sqlTable.getRowCount(); i++)
                tables.add(sqlTable.get(i, 0));
        });
        sql.close();

        modelTables.getModelClasses().forEach(modelClass -> {
            ModelTable modelTable = modelTables.get(modelClass);
            create(tables, modelTable, modelClass);
            memory(modelTable);
            sql.close();
        });
    }

    private void create(Set<String> tables, ModelTable modelTable, Class<? extends Model> modelClass) {
        if (tables.contains(modelTable.getTableName()))
            return;

        String[] array = read(modelClass);
        if (array == null)
            return;

        for (String string : array) {
            string = string.trim();
            if (string.length() == 0 || string.charAt(0) == '-')
                continue;

            sql.update(modelTable.getDataSource(), string, new Object[0]);
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

    private void memory(ModelTable modelTable) {
        if (modelTable.getMemoryName() == null || count(modelTable.getMemoryName()) == count(modelTable.getTableName()))
            return;

        int delete = sql.update("DELETE FROM " + modelTable.getMemoryName(), new Object[0]);
        int insert = sql.update("INSERT INTO " + modelTable.getMemoryName() + " SELECT * FROM " + modelTable.getTableName(), new Object[0]);
        if (logger.isInfoEnable())
            logger.info("同步内存表[{}:{}]数据[{}:{}]。", modelTable.getMemoryName(), delete, modelTable.getTableName(), insert);
    }

    private int count(String tableName) {
        SqlTable sqlTable = sql.query("SELECT COUNT(*) FROM " + tableName, null);

        return numeric.toInt(sqlTable.get(0, 0));
    }
}
