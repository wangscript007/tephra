package org.lpw.tephra.office.pptx;

import org.junit.Test;
import org.lpw.tephra.test.CoreTestSupport;
import org.lpw.tephra.util.Io;
import org.lpw.tephra.util.Json;

import javax.inject.Inject;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author lpw
 */
public class PptxWriterTest extends CoreTestSupport {
    @Inject
    private Io io;
    @Inject
    private Json json;
    @Inject
    private PptxWriter pptxWriter;

    @Test
    public void write() throws IOException {
        long time = System.currentTimeMillis();
        OutputStream outputStream = new FileOutputStream("/mnt/hgfs/share/ppt/demo-out.pptx");
        pptxWriter.write(outputStream, json.toObject(io.readAsString("target/pptx.json")), (obj) -> {
            try {
                return new FileInputStream("/mnt/hgfs/share/ppt/demo/ppt/media/image1.jpeg");
            } catch (FileNotFoundException e) {
                e.printStackTrace();

                return null;
            }
        });
        outputStream.close();
        System.out.println((System.currentTimeMillis() - time) / 1000.0);
    }
}
