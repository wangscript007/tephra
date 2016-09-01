package org.lpw.tephra.ctrl.template.json;

import net.sf.json.JSONObject;
import org.lpw.tephra.ctrl.Failure;
import org.lpw.tephra.ctrl.template.Template;
import org.lpw.tephra.ctrl.template.TemplateSupport;
import org.lpw.tephra.ctrl.template.Templates;
import org.lpw.tephra.util.Message;
import org.lpw.tephra.util.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author lpw
 */
@Controller("tephra.ctrl.template.json")
public class TemplateImpl extends TemplateSupport implements Template {
    @Autowired
    protected Validator validator;
    @Autowired
    protected Message message;

    @Override
    public String getType() {
        return Templates.JSON;
    }

    @Override
    protected String getDefaultContentType() {
        return "application/json";
    }

    @Override
    public void process(String name, Object data, OutputStream output) throws IOException {
        if (data instanceof Failure) {
            write(getFailure((Failure) data), output);

            return;
        }

        write(pack(data), output);
    }

    protected void write(Object data, OutputStream output) throws IOException {
        output.write(data.toString().getBytes("UTF-8"));
    }

    protected Object pack(Object object) {
        if (object instanceof JSONObject && ((JSONObject) object).has("code"))
            return object;

        JSONObject json = new JSONObject();
        json.put("code", 0);
        json.put("data", object);

        return json;
    }
}
