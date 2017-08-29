package org.lpw.tephra.script;

import org.junit.Assert;
import org.junit.Test;
import org.lpw.tephra.test.CoreTestSupport;
import org.lpw.tephra.util.Thread;
import org.lpw.tephra.util.TimeUnit;

import javax.inject.Inject;

/**
 * @author lpw
 */
public class ArgumentsTest extends CoreTestSupport {
    @Inject
    private Thread thread;
    @Inject
    private Engine engine;
    @Inject
    private Arguments arguments;

    @Test
    public void execute() throws Exception {
        thread.sleep(2, TimeUnit.Second);

        arguments.set("arg", "arg from java");
        Assert.assertEquals("arg from java", engine.execute("arguments_execute"));
        Assert.assertEquals("arg from javascript", arguments.get("arg"));
    }

    @Test
    public void all() throws Exception {
        thread.sleep(2, TimeUnit.Second);

        arguments.all().clear();
        int sum = 0;
        for (int i = 0; i < 10; i++) {
            arguments.set("arg" + i, i);
            sum += i;
        }
        Assert.assertEquals(sum * 1.0D, engine.execute("arguments_all"));
    }
}
