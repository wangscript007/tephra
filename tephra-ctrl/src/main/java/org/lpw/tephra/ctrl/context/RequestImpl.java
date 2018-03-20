package org.lpw.tephra.ctrl.context;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.tephra.ctrl.Coder;
import org.lpw.tephra.dao.model.Model;
import org.lpw.tephra.dao.model.ModelHelper;
import org.lpw.tephra.dao.model.ModelTables;
import org.lpw.tephra.util.Converter;
import org.lpw.tephra.util.DateTime;
import org.lpw.tephra.util.Json;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Numeric;
import org.lpw.tephra.util.Validator;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

/**
 * @author lpw
 */
@Controller("tephra.ctrl.context.request")
public class RequestImpl implements Request, RequestAware {
    @Inject
    private Validator validator;
    @Inject
    private Converter converter;
    @Inject
    private Numeric numeric;
    @Inject
    private DateTime dateTime;
    @Inject
    private Json json;
    @Inject
    private Logger logger;
    @Inject
    private ModelHelper modelHelper;
    @Inject
    private Optional<Coder> coder;
    private ThreadLocal<RequestAdapter> adapter = new ThreadLocal<>();

    @Override
    public String get(String name) {
        return adapter.get() == null ? null : adapter.get().get(name);
    }

    @Override
    public int getAsInt(String name) {
        return numeric.toInt(get(name));
    }

    @Override
    public int getAsInt(String name, int defaultValue) {
        return numeric.toInt(get(name), defaultValue);
    }

    @Override
    public long getAsLong(String name) {
        return numeric.toLong(get(name));
    }

    @Override
    public long getAsLong(String name, long defaultValue) {
        return numeric.toLong(get(name), defaultValue);
    }

    @Override
    public boolean getAsBoolean(String name) {
        return converter.toBoolean(get(name));
    }

    @Override
    public Date getAsDate(String name) {
        return dateTime.toDate(get(name));
    }

    @Override
    public java.sql.Date getAsSqlDate(String name) {
        Date date = getAsDate(name);

        return date == null ? null : new java.sql.Date(date.getTime());
    }

    @Override
    public String[] getAsArray(String name) {
        if (adapter.get() == null)
            return null;

        String[] array = adapter.get().getAsArray(name);

        return array == null ? converter.toArray(get(name), ",") : array;
    }

    @Override
    public JSONObject getAsJsonObject(String name) {
        return json.toObject(get(name));
    }

    @Override
    public JSONArray getAsJsonArray(String name) {
        return json.toArray(get(name));
    }

    @Override
    public Map<String, String> getMap() {
        if (adapter.get() == null)
            return null;

        Map<String, String> map = adapter.get().getMap();

        return coder.isPresent() ? coder.get().decode(map) : map;
    }

    @Override
    public String getFromInputStream() {
        return adapter.get() == null ? null : adapter.get().getFromInputStream();
    }

    @Override
    public <T extends Model> T setToModel(Class<T> modelClass) {
        return adapter.get() == null ? null : modelHelper.fromMap(getMap(), modelClass);
    }

    @Override
    public String getServerName() {
        return adapter.get() == null ? null : adapter.get().getServerName();
    }

    @Override
    public int getServerPort() {
        return adapter.get() == null ? 0 : adapter.get().getServerPort();
    }

    @Override
    public String getContextPath() {
        return adapter.get() == null ? null : adapter.get().getContextPath();
    }

    @Override
    public String getUrl() {
        return adapter.get() == null ? null : adapter.get().getUrl();
    }

    @Override
    public String getUri() {
        return adapter.get() == null ? null : adapter.get().getUri();
    }

    @Override
    public String getMethod() {
        return adapter.get() == null ? null : adapter.get().getMethod();
    }

    @Override
    public void set(RequestAdapter adapter) {
        this.adapter.set(adapter);
    }
}
