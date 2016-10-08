package org.lpw.tephra.ctrl;

import org.lpw.tephra.bean.ContextRefreshedListener;
import org.lpw.tephra.ctrl.console.Console;
import org.lpw.tephra.ctrl.context.Request;
import org.lpw.tephra.ctrl.context.Response;
import org.lpw.tephra.ctrl.execute.ExecuteInvocation;
import org.lpw.tephra.ctrl.execute.Executor;
import org.lpw.tephra.ctrl.execute.ExecutorHelper;
import org.lpw.tephra.ctrl.status.Status;
import org.lpw.tephra.ctrl.template.Template;
import org.lpw.tephra.ctrl.template.TemplateHelper;
import org.lpw.tephra.ctrl.template.Templates;
import org.lpw.tephra.ctrl.validate.Validators;
import org.lpw.tephra.dao.Commitable;
import org.lpw.tephra.util.Converter;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author lpw
 */
@Controller("tephra.ctrl.dispatcher")
public class DispatcherImpl implements Dispatcher, Forward, ContextRefreshedListener {
    @Autowired
    protected Validator validator;
    @Autowired
    protected Converter converter;
    @Autowired
    protected Logger logger;
    @Autowired
    protected Set<Commitable> commitables;
    @Autowired
    protected Request request;
    @Autowired
    protected Status status;
    @Autowired
    protected Console console;
    @Autowired
    protected Validators validators;
    @Autowired(required = false)
    protected List<Interceptor> interceptors;
    @Autowired
    protected ExecutorHelper executorHelper;
    @Autowired(required = false)
    protected Coder coder;
    @Autowired(required = false)
    protected Permit permit;
    @Autowired
    protected Templates templates;
    @Autowired
    protected TemplateHelper templateHelper;
    @Value("${tephra.ctrl.dispatcher.max:512}")
    protected int max;
    protected ThreadLocal<Long> time = new ThreadLocal<>();
    protected AtomicInteger counter = new AtomicInteger();
    protected ThreadLocal<Map<String, Object>> parameters = new ThreadLocal<>();
    protected ThreadLocal<Response> response = new ThreadLocal<>();

    @Override
    public void execute(Response response) {
        time.set(System.currentTimeMillis());
        String uri = request.getUri();
        if (logger.isDebugEnable())
            logger.debug("开始处理请求[{}:{}]。", uri, converter.toString(request.getMap()));

        boolean statusService = status.isStatus(uri);
        if (counter.incrementAndGet() > max && !statusService) {
            counter.decrementAndGet();
            write(Failure.Busy, response);

            logger.warn(null, "超过最大并发处理数[{}]，返回繁忙信息[{}]。", max);

            return;
        }

        boolean consoleService = console.isConsole(uri);
        if (!statusService && !consoleService)
            executorHelper.set(uri);
        if (!statusService && !consoleService && executorHelper.get() == null) {
            counter.decrementAndGet();
            write(Failure.Exception, response);

            logger.warn(null, "无法获得请求[{}]的处理服务！", uri);

            return;
        }

        this.response.set(response);
        execute(statusService, consoleService, response);
        commitables.forEach(Commitable::close);
        counter.decrementAndGet();

        if (logger.isDebugEnable())
            logger.debug("处理请求[{}]完成，耗时[{}]毫秒。", uri, System.currentTimeMillis() - time.get());
    }

    protected void execute(boolean statusService, boolean consoleService, Response response) {
        Object object;
        if (statusService)
            object = status.execute(counter.get());
        else if (consoleService)
            object = console.execute();
        else
            object = execute();
        write(object, response);
    }

    @Override
    public Object redirect(String uri) {
        if (logger.isDebugEnable())
            logger.debug("跳转到：{}。", uri);

        executorHelper.set(uri);

        return execute();
    }

    protected Object execute() {
        if (permit != null && !permit.allow()) {
            return Failure.NotPermit;
        }

        try {
            return new ExecuteInvocation(interceptors, validators, executorHelper.get()).invoke();
        } catch (Throwable e) {
            commitables.forEach(Commitable::rollback);
            logger.warn(e, "执行请求[{}:{}]时发生异常！", request.getUri(), executorHelper.get().getMethod());

            return Failure.Exception;
        }
    }

    protected void write(Object object, Response response) {
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
            response.setContentType(template.getContentType());
            OutputStream output = response.getOutputStream();
            if (coder == null) {
                template.process(view, object, output);

                return;
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            template.process(view, object, baos);
            baos.close();
            output.write(coder.encode(baos.toByteArray()));
        } catch (Exception e) {
            logger.warn(e, "返回输出结果时发生异常！");
        }
    }

    @Override
    public Object redirect(String uri, Map<String, Object> parameters) {
        this.parameters.set(parameters);

        return redirect(uri);
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public <T> T getParameter(String name) {
        Map<String, Object> parameters = getParameters();
        if (validator.isEmpty(parameters))
            return null;

        return (T) parameters.get(name);
    }

    @Override
    public Map<String, Object> getParameters() {
        return parameters.get();
    }

    @Override
    public void redirectTo(String url) {
        if (logger.isDebugEnable())
            logger.debug("跳转到：{}。", url);

        response.get().redirectTo(url);
    }

    @Override
    public int getContextRefreshedSort() {
        return 8;
    }

    @Override
    public void onContextRefreshed() {
        if (interceptors == null)
            interceptors = new ArrayList<>();
        Collections.sort(interceptors, (a, b) -> a.getSort() - b.getSort());
    }
}
