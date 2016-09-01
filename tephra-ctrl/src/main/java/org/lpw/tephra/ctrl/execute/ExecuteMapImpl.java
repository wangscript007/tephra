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

/**
 * @author lpw
 */
@Controller("tephra.ctrl.execute.map")
public class ExecuteMapImpl implements ExecuteMap, FailureCode, ContextRefreshedListener {
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
    protected Map<String, Executor> map;
    protected Map<String, String> codes;

    @Override
    public Executor get(String service) {
        Executor executor = map.get(service);
        if (executor != null)
            return executor;

        for (String regex : map.keySet())
            if (validator.isMatchRegex(regex, service))
                return map.get(regex);

        return null;
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

                Executor executor = new ExecutorImpl(BeanFactory.getBean(name), method, execute.validates(), templates.get(execute.type()), prefix + execute.template());
                String code = prefixCode + execute.code();
                for (String service : converter.toArray(execute.name(), ",")) {
                    String key = prefix + service;
                    map.put(key, executor);
                    codes.put(key, code);
                }
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
}
