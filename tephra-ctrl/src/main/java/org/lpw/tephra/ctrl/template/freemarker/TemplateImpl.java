package org.lpw.tephra.ctrl.template.freemarker;

import net.sf.json.JSONObject;
import org.lpw.tephra.ctrl.Failure;
import org.lpw.tephra.ctrl.context.Request;
import org.lpw.tephra.ctrl.template.Template;
import org.lpw.tephra.ctrl.template.TemplateSupport;
import org.lpw.tephra.ctrl.template.Templates;
import org.lpw.tephra.freemarker.Freemarker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author lpw
 */
@Controller("tephra.ctrl.template.freemarker")
public class TemplateImpl extends TemplateSupport implements Template {
    @Autowired
    protected Freemarker freemarker;
    @Autowired
    protected Request request;

    @Override
    public String getType() {
        return Templates.FREEMARKER;
    }

    @Override
    public String getContentType() {
        return "text/html";
    }

    @Override
    public void process(String name, Object data, OutputStream output) throws IOException {
        if (data instanceof Failure) {
            failure(getFailure((Failure) data), output);

            return;
        }

        if (data instanceof JSONObject && failure((JSONObject) data, output))
            return;

        freemarker.process(name, data, output);
    }

    protected boolean failure(JSONObject object, OutputStream output) throws IOException {
        if (object.has("code") && object.getInt("code") > 0) {
            output.write(object.toString().getBytes());

            return true;
        }

        return false;
    }
}
