package org.lpw.tephra.poi;

import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.lpw.tephra.test.CoreTestSupport;
import org.lpw.tephra.util.Io;
import org.lpw.tephra.util.Json;

import javax.inject.Inject;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * @author lpw
 */
public class PptxTest extends CoreTestSupport {
    @Inject
    private Io io;
    @Inject
    private Json json;
    @Inject
    private Pptx pptx;

    @Test
    public void write() throws Exception {
        JSONObject object = json.toObject(io.readAsString(getClass().getResourceAsStream("pptx.json")));
//        OutputStream outputStream = new FileOutputStream(getClass().getResource("pptx.json").getPath().replace("pptx.json", "test.pptx"));
        OutputStream outputStream = new FileOutputStream("/media/sf_share/test.pptx");
        pptx.write(object, outputStream);
        outputStream.close();
    }
}
