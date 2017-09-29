package org.lpw.tephra.ctrl.template.pptx;

import com.alibaba.fastjson.JSONObject;
import org.lpw.tephra.ctrl.template.TemplateSupport;
import org.lpw.tephra.ctrl.template.Templates;
import org.lpw.tephra.poi.Pptx;
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
    private Pptx pptx;

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
        if (data instanceof JSONObject)
            pptx.write((JSONObject) data, outputStream);
    }
}
