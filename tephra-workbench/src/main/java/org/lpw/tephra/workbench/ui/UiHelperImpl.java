package org.lpw.tephra.workbench.ui;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.lpw.tephra.bean.BeanFactory;
import org.lpw.tephra.bean.ContextRefreshedListener;
import org.lpw.tephra.cache.Cache;
import org.lpw.tephra.ctrl.validate.Validate;
import org.lpw.tephra.ctrl.validate.Validates;
import org.lpw.tephra.dao.model.Model;
import org.lpw.tephra.dao.model.ModelTable;
import org.lpw.tephra.dao.model.ModelTables;
import org.lpw.tephra.util.Context;
import org.lpw.tephra.util.Converter;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Message;
import org.lpw.tephra.util.Validator;
import org.lpw.tephra.workbench.model.DomainModel;
import org.lpw.tephra.workbench.model.StatusModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author lpw
 */
@Controller("tephra.workbench.ui.helper")
public class UiHelperImpl implements UiHelper, ContextRefreshedListener {
    private static final String CACHE_MENU = "tephra.workbench.metadata.helper.menu-locale:";
    private static final String CACHE_METADATA = "tephra.workbench.metadata.helper.metadata.uri-locale:";

    @Autowired
    protected Cache cache;
    @Autowired
    protected Validator validator;
    @Autowired
    protected Converter converter;
    @Autowired
    protected Context context;
    @Autowired
    protected Message message;
    @Autowired
    protected Logger logger;
    @Autowired
    protected ModelTables modelTables;
    protected List<Menu> menus;
    protected Map<String, JSONObject> metadatas;
    protected Map<String, Class<? extends Model>> models;
    protected Map<String, Set<String>> editables;
    protected Map<String, Validate[]> validates;
    protected Set<String> statuses;
    protected Set<String> domains;

    @Override
    public List<Menu> getMenus() {
        String key = CACHE_MENU + context.getLocale().toString();
        List<Menu> menus = cache.get(key);
        if (menus == null) {
            menus = new ArrayList<>();
            copy(this.menus, menus);
            cache.put(key, menus, false);
        }

        return menus;
    }

    protected void copy(List<Menu> source, List<Menu> target) {
        source.forEach(menu -> {
            Menu m = new Menu(menu.getGrade(), menu.getSort(), menu.getKey(), menu.getUri());
            m.setLabel(message.get(m.getKey()));
            if (!validator.isEmpty(menu.getChildren())) {
                m.setChildren(new ArrayList<>());
                copy(menu.getChildren(), m.getChildren());
            }
            target.add(m);
        });
    }

    @Override
    public JSONObject getMetadata(String uri) {
        String key = CACHE_METADATA + uri + "-" + context.getLocale().toString();
        JSONObject object = cache.get(key);
        if (object == null) {
            object = getMetadataFromMap(uri);
            if (object != null) {
                setMessageByKey(object);
                cache.put(key, object, false);
            }
        }

        return object;
    }

    protected JSONObject getMetadataFromMap(String uri) {
        JSONObject object = metadatas.get(uri.substring(0, uri.lastIndexOf('/') + 1));

        return object == null ? null : JSONObject.fromObject(object);
    }

    protected void setMessageByKey(JSONObject object) {
        setMessageByKey(object.getJSONArray("properties"), "labelKey");
    }

    protected void setMessageByKey(JSONArray array, String key) {
        for (int i = 0; i < array.size(); i++) {
            Object object = array.get(i);
            if (object instanceof JSONObject)
                setMessageByKey((JSONObject) object, key);
        }
    }

    protected void setMessageByKey(JSONObject object, String key) {
        object.put(key.substring(0, key.length() - 3), message.get(object.getString(key)));
    }

    @Override
    public Class<? extends Model> getModelClass(String uri) {
        return models.get(uri);
    }

    @Override
    public Set<String> getEditables(String uri) {
        return editables.get(uri);
    }

    @Override
    public Validate[] getValidates(String uri) {
        return validates.get(uri);
    }

    @Override
    public boolean isStatus(String uri) {
        return statuses.contains(uri);
    }

    @Override
    public boolean isDomain(String uri) {
        return domains.contains(uri);
    }

    @Override
    public int getContextRefreshedSort() {
        return 6;
    }

    @Override
    public void onContextRefreshed() {
        if (metadatas != null)
            return;

        menus = new ArrayList<>();
        metadatas = new HashMap<>();
        models = new HashMap<>();
        editables = new HashMap<>();
        validates = new HashMap<>();
        statuses = new HashSet<>();
        domains = new HashSet<>();
        modelTables.getModelClasses().forEach(modelClass -> {
            Entity entity = modelClass.getAnnotation(Entity.class);
            if (entity == null)
                return;

            String uri = "/" + entity.name().replace('.', '/') + "/";
            menu(modelClass, entity.name(), uri);
            JSONObject definition = new JSONObject();
            definition.put("name", entity.name());
            definition.put("uri", uri);
            Set<String> set = new HashSet<>();
            properties(modelTables.get(modelClass), definition, set, entity.name());
            metadatas.put(uri, definition);
            models.put(uri, modelClass);
            editables.put(uri, set);

            Validates vs = modelClass.getAnnotation(Validates.class);
            if (vs != null && !validator.isEmpty(vs.value()))
                validates.put(uri, vs.value());

            Model model = BeanFactory.getBean(modelClass);
            if (model instanceof StatusModel)
                statuses.add(uri);
            if (model instanceof DomainModel)
                domains.add(uri);
        });
        menus();
    }

    protected void menu(Class<? extends Model> modelClass, String key, String uri) {
        MenuDefinition definition = modelClass.getAnnotation(MenuDefinition.class);
        if (definition == null)
            return;

        if (definition.parent() > 0)
            menus.add(new Menu(1, definition.parent(), key.substring(0, key.lastIndexOf('.')), "/" + key.replace('.', '/') + "/"));
        menus.add(new Menu(2, definition.sort(), key, uri));
    }

    protected void properties(ModelTable modelTable, JSONObject definition, Set<String> editables, String prefix) {
        JSONArray properties = new JSONArray();
        Class<? extends Model> modelClass = modelTable.getModelClass();
        List<Property> list = new ArrayList<>();
        modelTable.getPropertyNames().forEach(name -> {
            PropertyDefinition propertyDefinition = null;
            try {
                propertyDefinition = modelClass.getMethod("get" + name).getAnnotation(PropertyDefinition.class);
            } catch (NoSuchMethodException e) {
                logger.warn(e, "无法获得Model类[{}]UI属性GET方法[{}]。", modelClass, name);
            }
            if (propertyDefinition == null)
                return;

            list.add(new Property(name, propertyDefinition));
            if (propertyDefinition.editable())
                editables.add(name);

        });
        Collections.sort(list);
        list.forEach(property -> {
            JSONObject object = new JSONObject();
            object.put("name", converter.toFirstLowerCase(property.getName()));
            object.put("labelKey", validator.isEmpty(property.getDefinition().labelKey()) ? toMessageKey(prefix, property.getName()) : property.getDefinition()
                    .labelKey());
            object.put("editable", property.getDefinition().editable());
            object.put("type", property.getDefinition().type().getName());
            properties.add(object);
        });
        definition.put("properties", properties);
    }

    protected String toMessageKey(String prefix, String name) {
        StringBuilder sb = new StringBuilder().append(prefix);
        boolean first = true;
        for (char ch : name.toCharArray()) {
            if (ch >= 'A' && ch <= 'Z') {
                sb.append(first ? '.' : '-').append((char) (ch - 'A' + 'a'));
                first = false;

                continue;
            }

            sb.append(ch);
        }

        return sb.toString();
    }

    protected void menus() {
        Collections.sort(menus);
        List<Menu> list = new ArrayList<>();
        Map<String, Menu> map = new HashMap<>();
        menus.forEach(menu -> {
            map.put(menu.getKey() + menu.getGrade(), menu);
            if (menu.getGrade() == 1) {
                list.add(menu);

                return;
            }

            Menu parent = map.get(menu.getKey().substring(0, menu.getKey().lastIndexOf('.')) + (menu.getGrade() - 1));
            if (parent == null)
                return;

            if (parent.getChildren() == null)
                parent.setChildren(new ArrayList<>());
            parent.getChildren().add(menu);
        });
        menus = list;
    }
}
