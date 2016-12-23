package org.lpw.tephra.test;

import org.junit.Before;
import org.junit.Test;
import org.lpw.tephra.dao.jdbc.Sql;
import org.lpw.tephra.util.Validator;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author lpw
 */
public class TephraTestSupport extends TestSupport {
    @Autowired
    protected Validator validator;
    @Autowired
    protected Sql sql;

    @Before
    public void before() throws IOException {
        String path = getClass().getResource("/").getPath();
        path = path.substring(0, path.lastIndexOf("/target/")) + "/src/";
        sql(path + "main/sql/create.sql");
        sql(path + "test/sql/mock.sql");
    }

    private void sql(String path) throws IOException {
        File file = new File(path);
        if (!file.exists() && !file.isFile())
            return;

        BufferedReader reader = new BufferedReader(new FileReader(file));
        StringBuilder sb = new StringBuilder();
        for (String line; (line = reader.readLine()) != null; ) {
            if (validator.isEmpty(line))
                continue;

            sb.append(line);
            if (line.trim().endsWith(";")) {
                sql.update(sb.toString(), new Object[0]);
                sb.delete(0, sb.length());
            }
        }
        reader.close();
    }
}
