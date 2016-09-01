package org.lpw.tephra.script;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lpw.tephra.util.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;

/**
 * @author lpw
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:**/spring.xml"})
public class ArgumentsTest {
    @Autowired
    protected Context context;
    @Autowired
    protected Engine engine;
    @Autowired
    protected Arguments arguments;

    @Test
    public void execute() throws Exception {
        context.setRoot(new File("src/test/resources/webapp").getCanonicalPath());
        arguments.set("arg", "arg from java");
        Assert.assertEquals("arg from java", engine.execute("arguments_execute"));
        Assert.assertEquals("arg from javascript", arguments.get("arg"));
    }

    @Test
    public void all() throws Exception {
        context.setRoot(new File("src/test/resources/webapp").getCanonicalPath());
        arguments.all().clear();
        int sum = 0;
        for (int i = 0; i < 10; i++) {
            arguments.set("arg" + i, i);
            sum += i;
        }
        Assert.assertEquals(sum * 1.0D, engine.execute("arguments_all"));
    }
}
