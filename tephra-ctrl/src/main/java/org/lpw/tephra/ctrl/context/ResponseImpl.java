package org.lpw.tephra.ctrl.context;

import org.lpw.tephra.ctrl.Coder;
import org.lpw.tephra.ctrl.execute.Executor;
import org.lpw.tephra.ctrl.execute.ExecutorHelper;
import org.lpw.tephra.ctrl.template.Template;
import org.lpw.tephra.ctrl.template.TemplateHelper;
import org.lpw.tephra.ctrl.template.Templates;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

/**
 * @auth lpw
 */
@Controller("tephra.ctrl.context.response")
public class ResponseImpl implements Response, ResponseAware {
    @Autowired
    protected Validator validator;
    @Autowired
    protected Logger logger;
    @Autowired
    protected ExecutorHelper executorHelper;
    @Autowired
    protected Templates templates;
    @Autowired
    protected TemplateHelper templateHelper;
    @Autowired(required = false)
    protected Coder coder;
    protected ThreadLocal<ResponseAdapter> adapter = new ThreadLocal<>();
    protected ThreadLocal<String> contentType = new ThreadLocal<>();

    @Override
    public void setContentType(String contentType) {
        this.contentType.set(contentType);
    }

    @Override
    public void setHeader(String name, String value) {
        adapter.get().setHeader(name, value);
    }

    @Override
    public OutputStream getOutputStream() {
        return adapter.get().getOutputStream();
    }

    @Override
    public void write(Object object) {
        if (object == null)
            return;

        try {
            Executor executor = executorHelper.get();
            Template template = executor == null ? templates.get() : executor.getTemplate();
            String view = executor == null ? null : executor.getView();
            if (!validator.isEmpty(templateHelper.getTemplate())) {
                view = templateHelper.getTemplate();
                templateHelper.setTemplate(null);
            }
            adapter.get().setContentType(validator.isEmpty(contentType.get()) ? template.getContentType() : contentType.get());
            if (coder == null) {
                template.process(view, object, getOutputStream());

                return;
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            template.process(view, object, baos);
            baos.close();
            getOutputStream().write(coder.encode(baos.toByteArray()));
        } catch (Exception e) {
            logger.warn(e, "返回输出结果时发生异常！");
        }
    }

    @Override
    public void redirectTo(String url) {
        if (logger.isDebugEnable())
            logger.debug("跳转到：{}。", url);

        adapter.get().redirectTo(url);
    }

    @Override
    public void sendError(int code) {
        adapter.get().sendError(code);
    }

    @Override
    public void set(ResponseAdapter adapter) {
        this.adapter.set(adapter);
    }
}
