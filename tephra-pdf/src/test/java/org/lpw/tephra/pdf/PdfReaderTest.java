package org.lpw.tephra.pdf;

import org.junit.Test;
import org.lpw.tephra.test.CoreTestSupport;
import org.lpw.tephra.util.Io;
import org.lpw.tephra.util.Json;

import javax.inject.Inject;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author lpw
 */
public class PdfReaderTest extends CoreTestSupport {
    @Inject
    private Io io;
    @Inject
    private Json json;
    @Inject
    private PdfReader pdfReader;

    @Test
    public void read() throws IOException {
        long time = System.currentTimeMillis();
        io.write("target/pdf.json", json.toBytes(pdfReader.read(new FileInputStream("/mnt/hgfs/share/pdf/x.pdf"),
                (mediaType, fileName, inputStream) -> "" + io.read(inputStream).length)));
        System.out.println((System.currentTimeMillis() - time) / 1000.0);
    }

    @Test
    public void jpegs() throws IOException {
        pdfReader.jpegs(new FileInputStream("/mnt/hgfs/share/pdf/scale.pdf"), (mediaType, fileName, inputStream) -> {
            try {
                io.copy(inputStream, new FileOutputStream("/mnt/hgfs/share/pdf/" + fileName));
            } catch (IOException e) {
                e.printStackTrace();
            }

            return fileName;
        }, 4, 1080, false);
    }
}
