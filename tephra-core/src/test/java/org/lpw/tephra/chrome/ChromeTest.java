package org.lpw.tephra.chrome;

import org.junit.Test;
import org.lpw.tephra.test.CoreTestSupport;
import org.lpw.tephra.util.Thread;
import org.lpw.tephra.util.TimeUnit;

import javax.inject.Inject;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author lpw
 */
public class ChromeTest extends CoreTestSupport {
    @Inject
    private Thread thread;
    @Inject
    private Chrome chrome;

    @Test
    public void pdf() throws IOException {
        thread.sleep(2, TimeUnit.Second);
        write("target/chrome.pdf", chrome.pdf("https://github.com/heisedebaise/tephra",
                5, 1024, 768, ""));
    }

    @Test
    public void png() throws IOException {
        thread.sleep(2, TimeUnit.Second);
        write("target/chrome.png", chrome.png("https://github.com/heisedebaise/tephra",
                5, 0, 0, 1366, 768));
    }

    @Test
    public void jpeg() throws IOException {
        thread.sleep(2, TimeUnit.Second);
        write("target/chrome.jpg", chrome.jpeg("https://github.com/heisedebaise/tephra",
                5, 0, 0, 1024, 768));
    }

    private void write(String output, byte[] bytes) throws IOException {
        if (bytes == null)
            return;

        OutputStream outputStream = new FileOutputStream(output);
        outputStream.write(bytes, 0, bytes.length);
        outputStream.close();
    }
}
