package org.lpw.tephra.office.pptx;

import org.junit.Test;
import org.lpw.tephra.test.TephraTestSupport;
import org.lpw.tephra.util.Io;

import javax.inject.Inject;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author lpw
 */
public class PptxReaderTest extends TephraTestSupport {
    @Inject
    private Io io;
    @Inject
    private PptxReader pptxReader;

    @Test
    public void read() throws IOException {
        io.write("target/demo.json", pptxReader.read(new FileInputStream("/mnt/hgfs/share/ppt/demo.pptx"),
                (type, inputStream) -> "").toJSONString().getBytes());
    }
}
