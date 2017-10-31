package org.lpw.tephra.chrome;

import org.junit.Test;
import org.lpw.tephra.test.CoreTestSupport;
import org.lpw.tephra.util.Thread;
import org.lpw.tephra.util.TimeUnit;

import javax.inject.Inject;

/**
 * @author lpw
 */
public class ChromeTest extends CoreTestSupport {
    @Inject
    private Thread thread;
    @Inject
    private Chrome chrome;

    @Test
    public void pdf() {
        thread.sleep(2, TimeUnit.Second);
        chrome.pdf("https://www.baidu.com", 5, 800, 600, "");
    }

    @Test
    public void png() {
        thread.sleep(2, TimeUnit.Second);
        chrome.png("https://www.baidu.com", 5, 0, 0, 800, 600);
    }

    @Test
    public void jpeg() {
        thread.sleep(2, TimeUnit.Second);
        chrome.jpeg("https://www.baidu.com", 5, 0, 0, 800, 600);
    }
}
