package org.lpw.tephra.ctrl.context;

import org.lpw.tephra.ctrl.Coder;
import org.lpw.tephra.dao.model.Model;
import org.lpw.tephra.dao.model.ModelHelper;
import org.lpw.tephra.dao.model.ModelTable;
import org.lpw.tephra.dao.model.ModelTables;
import org.lpw.tephra.util.Converter;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Security;
import org.lpw.tephra.util.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author lpw
 */
@Controller("tephra.ctrl.context.request")
public class RequestImpl implements Request, RequestAware {
    private static final String SIGN = "sign";
    private static final String SIGN_TIME = "sign-time";

    @Autowired
    protected Validator validator;
    @Autowired
    protected Converter converter;
    @Autowired
    protected Security security;
    @Autowired
    protected Logger logger;
    @Autowired
    protected ModelTables modelTables;
    @Autowired
    protected ModelHelper modelHelper;
    @Autowired(required = false)
    protected Coder coder;
    @Value("${tephra.ctrl.context.request.sign.key:}")
    protected String signKey;
    @Value("${tephra.ctrl.context.request.sign.time:10000}")
    protected long signTime;
    protected ThreadLocal<RequestAdapter> adapter = new ThreadLocal<>();

    @Override
    public String get(String name) {
        return adapter.get().get(name);
    }

    @Override
    public int getAsInt(String name) {
        return converter.toInt(get(name));
    }

    @Override
    public long getAsLong(String name) {
        return converter.toLong(get(name));
    }

    @Override
    public boolean getAsBoolean(String name) {
        return converter.toBoolean(get(name));
    }

    @Override
    public Date getAsDate(String name) {
        return converter.toDate(get(name));
    }

    @Override
    public String[] getAsArray(String name) {
        String[] array = adapter.get().getAsArray(name);

        return array == null ? converter.toArray(get(name), ",") : array;
    }

    @Override
    public Map<String, String> getMap() {
        Map<String, String> map = adapter.get().getMap();

        return coder == null ? map : coder.decode(map);
    }

    @Override
    public String getFromInputStream() {
        return adapter.get().getFromInputStream();
    }

    @Override
    public <T extends Model> T setToModel(T model) {
        Map<String, String> map = getMap();
        if (validator.isEmpty(map))
            return model;

        ModelTable modelTable = modelTables.get(model.getClass());
        try {
            for (String name : map.keySet()) {
                if ("id".equals(name)) {
                    model.setId(map.get(name));

                    continue;
                }

                fillToModel(modelTable, model, name, map.get(name));
            }
        } catch (Exception e) {
            logger.warn(e, "设置参数到Model时发生异常！");
        }

        return model;
    }

    protected <T extends Model> void fillToModel(ModelTable modelTable, T model, String name, String value) throws NoSuchMethodException, SecurityException {
        if ((name.endsWith("[id]") || name.endsWith(".id")) && name.indexOf('[') == name.lastIndexOf('[') && name.indexOf('.') == name.lastIndexOf('.')) {
            modelTable.set(model, name.substring(0, name.indexOf('[') + name.indexOf('.') + 1), value);

            return;
        }

        if (!validator.isMatchRegex("[a-z0-9A-Z]+", name) || value == null)
            return;

        modelTable.set(model, name, value);
    }

    @Override
    public boolean checkSign() {
        Map<String, String> map = getMap();
        if (map.get(SIGN) == null)
            return false;

        return System.currentTimeMillis() - converter.toLong(map.get(SIGN_TIME)) < signTime && getSign(map).equals(map.get(SIGN));
    }

    @Override
    public void putSign(Map<String, String> map) {
        map.put(SIGN_TIME, converter.toString(System.currentTimeMillis(), "0"));
        map.put(SIGN, getSign(map));
    }

    protected String getSign(Map<String, String> map) {
        List<String> list = new ArrayList<>(getMap().keySet());
        list.remove(SIGN);
        Collections.sort(list);

        StringBuilder sb = new StringBuilder();
        for (String key : list)
            sb.append(key).append('=').append(map.get(key)).append('&');
        sb.append(signKey);

        return security.md5(sb.toString());
    }

    @Override
    public String getServerName() {
        return adapter.get().getServerName();
    }

    @Override
    public int getServerPort() {
        return adapter.get().getServerPort();
    }

    @Override
    public String getContextPath() {
        return adapter.get().getContextPath();
    }

    @Override
    public String getUri() {
        return adapter.get().getUri();
    }

    @Override
    public String getMethod() {
        return adapter.get().getMethod();
    }

    @Override
    public void set(RequestAdapter adapter) {
        this.adapter.set(adapter);
    }
}
