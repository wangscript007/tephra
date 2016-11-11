package org.lpw.tephra.storage;

import org.lpw.tephra.bean.ContextRefreshedListener;
import org.lpw.tephra.util.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @auth lpw
 */
@Component("tephra.storages")
public class StoragesImpl implements Storages, ContextRefreshedListener {
    @Autowired
    protected Validator validator;
    @Autowired
    protected Set<Storage> set;
    @Value("${tephra.storage.default:disk}")
    protected String type;
    protected Map<String, Storage> map;

    @Override
    public Storage get() {
        return map.get(type);
    }

    @Override
    public Storage get(String type) {
        return map.get(validator.isEmpty(type) ? this.type : type);
    }

    @Override
    public int getContextRefreshedSort() {
        return 5;
    }

    @Override
    public void onContextRefreshed() {
        map = new HashMap<>();
        set.forEach(storage -> map.put(storage.getType(), storage));
    }
}
