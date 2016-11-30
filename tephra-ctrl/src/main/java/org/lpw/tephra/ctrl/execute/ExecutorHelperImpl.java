package org.lpw.tephra.ctrl.execute;

import org.lpw.tephra.bean.BeanFactory;
import org.lpw.tephra.bean.ContextRefreshedListener;
import org.lpw.tephra.ctrl.FailureCode;
import org.lpw.tephra.ctrl.context.Request;
import org.lpw.tephra.ctrl.template.Templates;
import org.lpw.tephra.util.Converter;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author lpw
 */
@Controller("tephra.ctrl.execute.map")
public class ExecutorHelperImpl implements ExecutorHelper, FailureCode, ContextRefreshedListener {
    @Autowired
    protected Validator validator;
    @Autowired
    protected Converter converter;
    @Autowired
    protected Logger logger;
    @Autowired
    protected Templates templates;
    @Autowired
    protected Request request;
    @Autowired(required = false)
    protected Set<ExecuteListener> listeners;
    protected Map<String, Executor> map;
    protected Map<String, String> codes;
    protected ThreadLocal<Executor> executors = new ThreadLocal<>();

    @Override
    public void set(String service) {
        if (setByKey(service))
            return;

        for (String regex : map.keySet())
            if (validator.isMatchRegex(regex, service) && setByKey(regex))
                return;
    }

    protected boolean setByKey(String key) {
        Executor executor = map.get(key);
        if (executor != null) {
            executors.set(executor);

            return true;
        }

        return false;
    }

    @Override
    public Executor get() {
        return executors.get();
    }

    @Override
    public int get(int code) {
        return get(request.getUri(), code);
    }

    @Override
    public int get(String uri, int code) {
        String prefix = codes.get(uri);
        if (prefix != null)
            return getCode(prefix, code);

        for (String regex : codes.keySet())
            if (validator.isMatchRegex(regex, uri))
                return getCode(codes.get(regex), code);

        return code;
    }

    protected int getCode(String prefix, int code) {
        int n = converter.toInt(prefix + converter.toString(code, "00"));

        return n == 0 ? -1 : n;
    }

    @Override
    public int getContextRefreshedSort() {
        return 8;
    }

    @Override
    public void onContextRefreshed() {
        if (map != null)
            return;

        map = new HashMap<>();
        codes = new HashMap<>();
        for (String name : BeanFactory.getBeanNames()) {
            Class<?> clazz = BeanFactory.getBeanClass(name);
            Execute classExecute = clazz.getAnnotation(Execute.class);
            String prefix = classExecute == null ? "" : classExecute.name();
            String prefixCode = classExecute == null ? "" : classExecute.code();
            for (Method method : clazz.getDeclaredMethods()) {
                Execute execute = method.getAnnotation(Execute.class);
                if (execute == null || validator.isEmpty(execute.name()))
                    continue;

                Executor executor = new ExecutorImpl(BeanFactory.getBean(name), method, getKey(classExecute, execute),
                        execute.validates(), templates.get(execute.type()), prefix + execute.template());
                String code = prefixCode + execute.code();
                for (String service : converter.toArray(execute.name(), ",")) {
                    String key = prefix + service;
                    map.put(key, executor);
                    codes.put(key, code);
                }
                if (!validator.isEmpty(listeners))
                    listeners.forEach(listener -> listener.definition(classExecute, execute));
            }
        }

        if (logger.isInfoEnable()) {
            StringBuilder sb = new StringBuilder().append("共[").append(map.size()).append("]个服务[");
            boolean hasElement = false;
            for (String key : map.keySet()) {
                if (hasElement)
                    sb.append(',');
                sb.append(key);
                hasElement = true;
            }
            logger.info(sb.append("]。").toString());
        }
    }

    protected String getKey(Execute classExecute, Execute execute) {
        if (!validator.isEmpty(execute.key()))
            return execute.key();

        return classExecute == null ? "" : classExecute.key();
    }
}
