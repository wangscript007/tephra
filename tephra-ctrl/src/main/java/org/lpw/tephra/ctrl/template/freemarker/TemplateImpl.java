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
    @Value("${tephra.ctrl.template.freemarker.not-permit:not-permit}")
    protected String notPermit;
    @Value("${tephra.ctrl.template.freemarker.failure:failure}")
    protected String failure;

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
            name = data == Failure.NotPermit ? notPermit : failure;
            StringBuilder root = new StringBuilder();
            for (char ch : request.getUri().toCharArray())
                if (ch == '/')
                    root.append("../");
            root.delete(0, 3);
            JSONObject object = getFailure((Failure) data);
            object.put("root", root.toString());
            data = object;
        }
        freemarker.process(name, data, output);
    }
}
