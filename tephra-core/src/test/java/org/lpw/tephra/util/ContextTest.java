package org.lpw.tephra.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.lang.Thread;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author lpw
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:**/spring.xml"})
public class ContextTest {
    @Autowired
    protected Context context;

    @Test
    public void setRoot() {
        context.setRoot("root");
        assertEquals("root", ((ContextImpl) context).root);
        assertTrue(((ContextImpl) context).map.isEmpty());
    }

    @Test
    public void getAbsolutePath() {
        context.setRoot("root");
        String root = new File("").getAbsolutePath() + "/";
        for (int i = 0; i < 10; i++)
            assertEquals(root + "root/path", context.getAbsolutePath("path"));
        for (int i = 0; i < 10; i++)
            assertEquals(root + "root/absolute-path", context.getAbsolutePath("absolute-path"));
    }

    @Test
    public void local() {
        assertEquals(Locale.getDefault(), context.getLocale());
        context.setLocale(Locale.ENGLISH);
        assertEquals(Locale.ENGLISH, context.getLocale());
        assertEquals(Locale.ENGLISH, context.getLocale());

        Locale[] locales = new Locale[]{Locale.ENGLISH, Locale.CANADA, Locale.CHINA};
        Thread[] threads = new Thread[locales.length];
        for (int i = 0; i < locales.length; i++) {
            final Locale locale = locales[i];
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 100; j++) {
                    context.setLocale(locale);
                    try {
                        Thread.sleep(5L);
                    } catch (InterruptedException e) {
                        Assert.assertTrue(false);
                    }
                    assertEquals(locale, context.getLocale());
                }
            });
        }
        for (Thread thread : threads)
            thread.start();

        try {
            Thread.sleep(200 * 5L);
        } catch (InterruptedException e) {
            Assert.assertTrue(false);
        }
    }
}
