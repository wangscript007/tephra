package org.lpw.tephra.office.pptx;

import org.junit.Test;
import org.lpw.tephra.test.TephraTestSupport;

import javax.inject.Inject;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author lpw
 */
public class PptxReaderTest extends TephraTestSupport {
    @Inject
    private PptxReader pptxReader;

    @Test
    public void read() throws IOException {
        pptxReader.read(new FileInputStream("/mnt/hgfs/share/ppt/demo.pptx"));
    }
}
