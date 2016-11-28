package org.lpw.tephra.storage;

import org.lpw.tephra.bean.BeanFactory;
import org.lpw.tephra.bean.ContextRefreshedListener;
import org.lpw.tephra.scheduler.SecondsJob;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @auth lpw
 */
@Component("tephra.storages")
public class StoragesImpl implements Storages, ContextRefreshedListener, SecondsJob {
    @Autowired
    protected Validator validator;
    @Autowired
    protected Logger logger;
    @Value("${tephra.storage.default:disk}")
    protected String type;
    protected Map<String, Storage> storages;
    protected Map<String, String> types;
    protected Map<String, StorageListener> listeners;
    protected Map<String, Long> times;

    @Override
    public Storage get() {
        return storages.get(type);
    }

    @Override
    public Storage get(String type) {
        return storages.get(validator.isEmpty(type) ? this.type : type);
    }

    @Override
    public int getContextRefreshedSort() {
        return 5;
    }

    @Override
    public void onContextRefreshed() {
        storages = new HashMap<>();
        BeanFactory.getBeans(Storage.class).forEach(storage -> storages.put(storage.getType(), storage));

        Collection<StorageListener> listeners = BeanFactory.getBeans(StorageListener.class);
        if (validator.isEmpty(listeners))
            return;

        types = new HashMap<>();
        this.listeners = new HashMap<>();
        times = new HashMap<>();
        listeners.forEach(listener -> {
            if (validator.isEmpty(listener.getScanPathes()))
                return;

            for (String path : listener.getScanPathes()) {
                if (types.containsKey(path))
                    logger.warn(null, "监听路径[{}]已存在！", path);

                types.put(path, validator.isEmpty(listener.getStorageType()) ? type : listener.getStorageType());
                this.listeners.put(path, listener);

                if (logger.isDebugEnable())
                    logger.debug("启动文件[{}]变化监听。", path);
            }
        });
    }

    @Override
    public void executeSecondsJob() {
        if (types == null)
            return;

        types.forEach((path, type) -> {
            String absolutePath = get(types.get(path)).getAbsolutePath(path);
            if (validator.isEmpty(absolutePath))
                return;

            long time = get(type).lastModified(absolutePath);
            Long cacheTime = times.get(path);
            if (cacheTime != null && cacheTime >= time)
                return;

            times.put(path, time);
            listeners.get(path).onStorageChanged(path, absolutePath);
        });
    }
}
