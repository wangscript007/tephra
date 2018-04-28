package org.lpw.tephra.ctrl.context;

import org.lpw.tephra.ctrl.Coder;
import org.lpw.tephra.ctrl.execute.Executor;
import org.lpw.tephra.ctrl.execute.ExecutorHelper;
import org.lpw.tephra.ctrl.template.Template;
import org.lpw.tephra.ctrl.template.TemplateHelper;
import org.lpw.tephra.ctrl.template.Templates;
import org.lpw.tephra.util.Context;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Validator;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Optional;

/**
 * @author lpw
 */
@Controller("tephra.ctrl.context.response")
public class ResponseImpl implements Response, ResponseAware {
    private static final String ADAPTER = "tephra.ctrl.context.response.adapter";
    private static final String CONTENT_TYPE = "tephra.ctrl.context.response.content-type";

    @Inject
    private Context context;
    @Inject
    private Validator validator;
    @Inject
    private Logger logger;
    @Inject
    private ExecutorHelper executorHelper;
    @Inject
    private Templates templates;
    @Inject
    private TemplateHelper templateHelper;
    @Inject
    private Optional<Coder> coder;

    @Override
    public void setContentType(String contentType) {
        if (logger.isDebugEnable())
            logger.debug("设置Content-Type[{}]", contentType);

        context.putThreadLocal(CONTENT_TYPE, contentType);
        getAdapter().setContentType(contentType);
    }

    @Override
    public void setHeader(String name, String value) {
        getAdapter().setHeader(name, value);
    }

    @Override
    public OutputStream getOutputStream() {
        return getAdapter().getOutputStream();
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
            setContentType(template);
            if (!coder.isPresent()) {
                template.process(view, object, getOutputStream());
                getAdapter().send();

                return;
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            template.process(view, object, baos);
            baos.close();
            getOutputStream().write(coder.get().encode(baos.toByteArray()));
            getAdapter().send();
        } catch (Exception e) {
            logger.warn(e, "返回输出结果时发生异常！");
        }
    }

    private void setContentType(Template template) {
        if (!validator.isEmpty(context.getThreadLocal(CONTENT_TYPE)))
            return;

        if (logger.isDebugEnable())
            logger.debug("使用Content-Type[{}]", template.getContentType());
        getAdapter().setContentType(template.getContentType());
    }

    @Override
    public void redirectTo(String url) {
        if (logger.isDebugEnable())
            logger.debug("跳转到：{}。", url);

        getAdapter().redirectTo(url);
    }

    @Override
    public void sendError(int code) {
        getAdapter().sendError(code);
    }

    private ResponseAdapter getAdapter() {
        return context.getThreadLocal(ADAPTER);
    }

    @Override
    public void set(ResponseAdapter adapter) {
        context.putThreadLocal(ADAPTER, adapter);
    }
}
