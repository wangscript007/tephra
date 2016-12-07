package org.lpw.tephra.script;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

/**
 * @author lpw
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:**/spring.xml"})
public class EngineTest {
    @Autowired
    protected Engine engine;

    @Test
    public void execute() throws IOException {
        Assert.assertEquals("message from javascript", engine.execute("engine_execute"));
    }
}
