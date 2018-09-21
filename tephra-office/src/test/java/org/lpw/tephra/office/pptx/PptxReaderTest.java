package org.lpw.tephra.office.pptx;

import org.junit.Test;
import org.lpw.tephra.office.GeometryConverter;
import org.lpw.tephra.test.CoreTestSupport;
import org.lpw.tephra.util.Io;
import org.lpw.tephra.util.Json;

import javax.inject.Inject;
import java.io.File;
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
        io.write("target/pptx.json", json.toBytes(pptxReader.read(new FileInputStream("/mnt/hgfs/share/ppt/tablex.pptx"),
                (mediaType, fileName, inputStream) -> "" + io.read(inputStream).length, new GeometryConverter() {
                    @Override
                    public String getGeometryImage(String type) {
                        return null;
                    }

                    @Override
                    public String saveGeometryImage(String type, File file,int width,int height) {
                        return null;
                    }
                })));
        System.out.println((System.currentTimeMillis() - time) / 1000.0);
    }
}
