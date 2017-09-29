package org.lpw.tephra.ctrl.template.pptx;

import com.alibaba.fastjson.JSONObject;
import org.lpw.tephra.ctrl.context.Response;
import org.lpw.tephra.ctrl.template.TemplateSupport;
import org.lpw.tephra.ctrl.template.Templates;
import org.lpw.tephra.poi.Pptx;
import org.lpw.tephra.util.Context;
import org.lpw.tephra.util.Converter;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author lpw
 */
@Controller("tephra.ctrl.template.pptx")
public class TemplateImpl extends TemplateSupport {
    @Inject
    private Context context;
    @Inject
    private Converter converter;
    @Inject
    private Pptx pptx;
    @Inject
    private Response response;

    @Override
    public String getType() {
        return Templates.PPTX;
    }

    @Override
    public String getContentType() {
        return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
    }

    @Override
    public void process(String name, Object data, OutputStream outputStream) throws IOException {
        if (!(data instanceof JSONObject))
            return;

        JSONObject object = (JSONObject) data;
        if (object.containsKey("filename"))
            response.setHeader("Content-Disposition", "attachment; filename*=" + context.getCharset(null) + "''" + converter.encodeUrl(object.getString("filename"), null) + ".pptx");
        pptx.write(object, outputStream);
    }
}
