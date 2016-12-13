package org.lpw.tephra.ctrl.template.string;

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
@Controller("tephra.ctrl.template.string")
public class TemplateImpl extends TemplateSupport implements Template {
    @Autowired
    protected Validator validator;
    @Autowired
    protected Message message;

    @Override
    public String getType() {
        return Templates.STRING;
    }

    @Override
    public String getContentType() {
        return "text/plain";
    }

    @Override
    public void process(String name, Object data, OutputStream outputStream) throws IOException {
        if (data instanceof Failure) {
            Failure failure = (Failure) data;
            write(getFailureCode(failure) + ":" + message.get(failure.getMessageKey()), outputStream);

            return;
        }

        write(data, outputStream);
    }

    protected void write(Object data, OutputStream outputStream) throws IOException {
        outputStream.write(data.toString().getBytes("UTF-8"));
    }
}
