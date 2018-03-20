package org.lpw.tephra.poi;

import com.alibaba.fastjson.JSONArray;
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
public class PptxTest extends CoreTestSupport implements StreamWriter {
    @Inject
    private Io io;
    @Inject
    private Coder coder;
    @Inject
    private Pptx pptx;

//    @Test
    public void read() throws IOException {
        InputStream fileInputStream = new FileInputStream("/mnt/hgfs/share/ppt/6635.pptx");
        JSONObject object = pptx.read(fileInputStream, this);
        fileInputStream.close();
        JSONArray array=new JSONArray();
        array.add(object);
        io.write("/mnt/hgfs/share/ppt/import.json", array.toJSONString().getBytes());
    }

    @Override
    public String write(String contentType, String filename, InputStream inputStream) throws IOException {
        return write(contentType, filename, io.read(inputStream));
    }

    @Override
    public String write(String contentType, String filename, byte[] bytes) throws IOException {
        return "data:" + contentType + ";base64," + coder.encodeBase64(bytes);
    }
}
