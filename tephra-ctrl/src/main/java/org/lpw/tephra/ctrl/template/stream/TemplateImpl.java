package org.lpw.tephra.ctrl.template.stream;

import org.lpw.tephra.ctrl.Failure;
import org.lpw.tephra.ctrl.template.Template;
import org.lpw.tephra.ctrl.template.TemplateSupport;
import org.lpw.tephra.ctrl.template.Templates;
import org.lpw.tephra.util.Context;
import org.lpw.tephra.util.Io;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author lpw
 */
@Controller("tephra.ctrl.template.stream")
public class TemplateImpl extends TemplateSupport implements Template {
    @Autowired
    protected Io io;
    @Autowired
    protected Context context;
    @Value("${tephra.ctrl.template.stream.failure:failure.jpg}")
    protected String failure;

    @Override
    public String getType() {
        return Templates.STREAM;
    }

    @Override
    protected String getDefaultContentType() {
        return null;
    }

    @Override
    public void process(String name, Object data, OutputStream output) throws IOException {
        if (data instanceof Failure) {
            InputStream input = new FileInputStream(context.getAbsolutePath(failure));
            io.copy(input, output);
            input.close();

            return;
        }

        output.write((byte[]) data);
    }
}
