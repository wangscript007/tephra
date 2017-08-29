package org.lpw.tephra.script;

import org.junit.Assert;
import org.junit.Test;
import org.lpw.tephra.test.CoreTestSupport;
import org.lpw.tephra.util.Thread;
import org.lpw.tephra.util.TimeUnit;

import javax.inject.Inject;
import java.io.IOException;

/**
 * @author lpw
 */
public class EngineTest extends CoreTestSupport {
    @Inject
    private Thread thread;
    @Inject
    private Engine engine;

    @Test
    public void execute() throws IOException {
        thread.sleep(2, TimeUnit.Second);
        Assert.assertEquals("message from javascript", engine.execute("engine_execute"));
    }
}
