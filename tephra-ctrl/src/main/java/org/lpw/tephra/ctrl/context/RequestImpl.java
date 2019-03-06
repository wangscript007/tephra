package org.lpw.tephra.ctrl.context;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.tephra.ctrl.Coder;
import org.lpw.tephra.dao.model.Model;
import org.lpw.tephra.dao.model.ModelHelper;
import org.lpw.tephra.util.Context;
import org.lpw.tephra.util.Converter;
import org.lpw.tephra.util.DateTime;
import org.lpw.tephra.util.Json;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Numeric;
import org.lpw.tephra.util.Validator;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

/**
 * @author lpw
 */
@Controller("tephra.ctrl.context.request")
public class RequestImpl implements Request, RequestAware {
    private static final String ADAPTER = "tephra.ctrl.context.request.adapter";

    @Inject
    private Context context;
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

    @Override
    public String getId() {
        return getAdapter() == null ? null : getAdapter().getId();
    }

    @Override
    public String get(String name) {
        return getAdapter() == null ? null : getAdapter().get(name);
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
    public Timestamp getAsTimestamp(String name) {
        return dateTime.toTime(get(name));
    }

    @Override
    public String[] getAsArray(String name) {
        if (getAdapter() == null)
            return null;

        String[] array = getAdapter().getAsArray(name);
        if (validator.isEmpty(array))
            return converter.toArray(get(name), ",");

        return array.length == 1 ? converter.toArray(array[0], ",") : array;
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
        if (getAdapter() == null)
            return null;

        Map<String, String> map = getAdapter().getMap();

        return coder.isPresent() ? coder.get().decode(map) : map;
    }

    @Override
    public String getFromInputStream() {
        return getAdapter() == null ? null : getAdapter().getFromInputStream();
    }

    @Override
    public <T extends Model> T setToModel(Class<T> modelClass) {
        return getAdapter() == null ? null : modelHelper.fromMap(getMap(), modelClass);
    }

    @Override
    public String getServerName() {
        return getAdapter() == null ? null : getAdapter().getServerName();
    }

    @Override
    public int getServerPort() {
        return getAdapter() == null ? 0 : getAdapter().getServerPort();
    }

    @Override
    public String getContextPath() {
        return getAdapter() == null ? null : getAdapter().getContextPath();
    }

    @Override
    public String getUrl() {
        return getAdapter() == null ? null : getAdapter().getUrl();
    }

    @Override
    public String getUri() {
        return getAdapter() == null ? null : getAdapter().getUri();
    }

    @Override
    public String getMethod() {
        return getAdapter() == null ? null : getAdapter().getMethod();
    }

    private RequestAdapter getAdapter() {
        return context.getThreadLocal(ADAPTER);
    }

    @Override
    public void set(RequestAdapter adapter) {
        context.putThreadLocal(ADAPTER, adapter);
    }
}
