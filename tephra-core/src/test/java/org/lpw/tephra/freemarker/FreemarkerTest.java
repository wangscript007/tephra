package org.lpw.tephra.freemarker;

import org.junit.Assert;
import org.junit.Test;
import org.lpw.tephra.test.CoreTestSupport;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lpw
 */
public class FreemarkerTest extends CoreTestSupport {
    @Inject
    private Freemarker freemarker;

    @Test
    public void processString() {
        freemarker.putStringTemplate("hello", "hi ${data.name}");
        Map<String, String> map = new HashMap<>();
        map.put("name", "freemarker");
        Assert.assertEquals("hi freemarker", freemarker.process("hello", map));
        Assert.assertEquals("hello freemarker", freemarker.process("index", map));

        freemarker.removeStringTemplate("hello");
        Assert.assertEquals("", freemarker.process("hello", map));
        Assert.assertEquals("hello freemarker", freemarker.process("index", map));
    }
}
