package org.lpw.tephra.ctrl.template.stream;

import com.alibaba.fastjson.JSONObject;
import org.lpw.tephra.ctrl.Failure;
import org.lpw.tephra.ctrl.context.Response;
import org.lpw.tephra.ctrl.template.TemplateSupport;
import org.lpw.tephra.ctrl.template.Templates;
import org.lpw.tephra.util.Json;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author lpw
 */
@Controller("tephra.ctrl.template.stream")
public class TemplateImpl extends TemplateSupport {
    @Inject
    private Json json;
    @Inject
    private Response response;

    @Override
    public String getType() {
        return Templates.STREAM;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public void process(String name, Object data, OutputStream outputStream) throws IOException {
        if (data instanceof Failure)
            data = getFailure((Failure) data);
        if (data instanceof JSONObject) {
            response.setContentType("application/json");
            data = json.toBytes(data);
        }

        outputStream.write((byte[]) data);
    }
}
