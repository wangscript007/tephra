package org.lpw.tephra.office.pptx;

import org.junit.Test;
import org.lpw.tephra.test.TephraTestSupport;

import javax.inject.Inject;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author lpw
 */
public class ReaderTest extends TephraTestSupport {
    @Inject
    private Reader reader;

    @Test
    public void read() throws IOException {
        reader.read(new FileInputStream("src/test/resources/001.pptx"));
    }
}
