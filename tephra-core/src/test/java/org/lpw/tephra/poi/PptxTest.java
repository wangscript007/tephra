package org.lpw.tephra.poi;

import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.lpw.tephra.test.CoreTestSupport;
import org.lpw.tephra.util.Coder;
import org.lpw.tephra.util.Io;

import javax.inject.Inject;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author lpw
 */
public class PptxTest extends CoreTestSupport {
    @Inject
    private Io io;
    @Inject
    private Coder coder;
    @Inject
    private Pptx pptx;

    @Test
    public void read() throws IOException {
        InputStream inputStream = new FileInputStream("/mnt/hgfs/share/ppt/001.pptx");
        JSONObject object = pptx.read(inputStream, new StreamWriter() {
            @Override
            public String write(String contentType, String filename, InputStream inputStream) throws IOException {
                return "data:" + contentType + ";base64," + coder.encodeBase64(io.read(inputStream));
            }
        });
        inputStream.close();
        io.write("/mnt/hgfs/share/ppt/import.json", object.toJSONString().getBytes());
    }
}
