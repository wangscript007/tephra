package org.lpw.tephra.office.pptx;

import org.junit.Test;
import org.lpw.tephra.test.CoreTestSupport;
import org.lpw.tephra.util.Io;
import org.lpw.tephra.util.Json;

import javax.inject.Inject;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author lpw
 */
public class PptxReaderTest extends CoreTestSupport {
    @Inject
    private Io io;
    @Inject
    private Json json;
    @Inject
    private PptxReader pptxReader;

    @Test
    public void read() throws IOException {
        long time = System.currentTimeMillis();
        io.write("target/pptx.json", json.toBytes(pptxReader.read(new FileInputStream("/mnt/hgfs/share/ppt/xxx.pptx"),
                (mediaType, fileName, inputStream) -> io.readAsString(inputStream))));
        System.out.println((System.currentTimeMillis() - time) / 1000.0);
    }
}
