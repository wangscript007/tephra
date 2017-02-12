package org.lpw.tephra.dao.model;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.tephra.bean.BeanFactory;
import org.lpw.tephra.util.Converter;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Validator;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author lpw
 */
@Repository("tephra.model.helper")
public class ModelHelperImpl implements ModelHelper {
    private Validator validator;
    private Converter converter;
    private Logger logger;
    private ModelTables modelTables;

    public ModelHelperImpl(Validator validator, Converter converter, Logger logger, ModelTables modelTables) {
        this.validator = validator;
        this.converter = converter;
        this.logger = logger;
        this.modelTables = modelTables;
    }

    @Override
    public Object get(Model model, String name) {
        return modelTables.get(getModelClass(model.getClass())).get(model, name);
    }

    @Override
    public void set(Model model, String name, Object value) {
        modelTables.get(getModelClass(model.getClass())).set(model, name, value);
    }

    @Override
    public <T extends Model> JSONObject toJson(T model) {
        return toJson(model, new HashSet<>());
    }

    @Override
    public <T extends Model> JSONObject toJson(T model, Set<String> ignores) {
        if (model == null)
            return null;

        if (ignores == null)
            ignores = new HashSet<>();

        JSONObject object = new JSONObject();
        ModelTable modelTable = modelTables.get(getModelClass(model.getClass()));
        if (!ignores.contains("id"))
            object.put("id", model.getId());
        for (String name : modelTable.getPropertyNames()) {
            if (ignores.contains(name))
                continue;

            Jsonable jsonable = modelTable.getJsonable(name);
            if (jsonable == null)
                continue;

            Object json = getJson(modelTable, name, modelTable.get(model, name), jsonable);
            if (json != null)
                object.put(name, json);
        }

        return object;
    }

    @SuppressWarnings("unchecked")
    private <T extends Model> Object getJson(ModelTable modelTable, String name, Object value, Jsonable jsonable) {
        if (value == null)
            return null;

        if (value instanceof Number)
            return value;

        if (value instanceof Model)
            return toJson((T) value);

        if (value instanceof Collection) {
            JSONArray array = new JSONArray();
            if (!validator.isEmpty(value))
                for (Object object : (Collection<?>) value)
                    array.add(getJson(modelTable, name, object, jsonable));

            return array;
        }

        if (value instanceof Date) {
            Class<?> type = modelTable.getType(name);
            if (type == null)
                return null;

            Date date = (Date) value;
            if (Timestamp.class.equals(type))
                return converter.toString(new Timestamp(date.getTime()));

            return converter.toString(new java.sql.Date(date.getTime()));
        }

        String format = jsonable.format();
        if (!validator.isEmpty(format)) {
            if (format.startsWith("number.")) {
                int[] ns = converter.toInts(format.substring(7));
                return converter.toString(value, ns[0], ns[1]);
            }
        }

        return converter.toString(value);
    }

    @SuppressWarnings("unchecked")
    private <T extends Model> Class<T> getModelClass(Class<T> modelClass) {
        if (modelClass.getName().endsWith("Model"))
            return modelClass;

        return getModelClass((Class<T>) modelClass.getSuperclass());
    }

    @Override
    public <T extends Model> JSONArray toJson(Collection<T> models) {
        return toJson(models, new HashSet<>());
    }

    @Override
    public <T extends Model> JSONArray toJson(Collection<T> models, Set<String> ignores) {
        JSONArray array = new JSONArray();
        if (validator.isEmpty(models))
            return array;

        models.forEach(model -> array.add(toJson(model, ignores)));

        return array;
    }

    @Override
    public <T extends Model> T fromJson(JSONObject json, Class<T> modelClass) {
        if (json == null || modelClass == null)
            return null;

        ModelTable modelTable = modelTables.get(modelClass);
        T model = BeanFactory.getBean(modelClass);
        if (json.containsKey("id"))
            model.setId(json.getString("id"));
        for (Object key : json.keySet())
            modelTable.set(model, (String) key, json.get(key));

        return model;
    }

    @Override
    public <T extends Model> void copy(T source, T target, boolean containId) {
        if (source == null || target == null) {
            logger.warn(null, "复制Model源[{}]或目标[{}]为null，无法进行复制！", source, target);

            return;
        }

        modelTables.get(source.getClass()).copy(source, target, containId);
    }

    @Override
    public <T extends Model> String toString(T model) {
        return modelTables.get(getModelClass(model.getClass())).toString(model);
    }
}
