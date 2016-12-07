package org.lpw.tephra.test;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lpw.tephra.dao.jdbc.Sql;
import org.lpw.tephra.dao.model.Model;
import org.lpw.tephra.dao.orm.lite.LiteOrm;
import org.lpw.tephra.dao.orm.lite.LiteQuery;
import org.lpw.tephra.util.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author lpw
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:**/spring.xml"})
public class TephraTestSupport {
    @Autowired
    protected Validator validator;
    @Autowired
    protected Sql sql;
    @Autowired
    protected LiteOrm liteOrm;

    @Before
    public void before() throws IOException {
        String path = getClass().getResource("/").getPath();
        path = path.substring(0, path.lastIndexOf("/target/")) + "/src/test/sql/";
        sql(path + "mock.sql");
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

    /**
     * 清空数据。
     *
     * @param modelClass Model类。
     */
    protected void clean(Class<? extends Model> modelClass) {
        liteOrm.delete(new LiteQuery(modelClass), null);
    }

    @Test
    public void test() {
    }
}
