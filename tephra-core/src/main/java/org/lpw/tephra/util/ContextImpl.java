package org.lpw.tephra.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lpw
 */
@Component("tephra.util.context")
public class ContextImpl implements Context {
    @Autowired
    protected Logger logger;
    protected String root;
    protected Map<String, String> map = new ConcurrentHashMap<>();
    protected ThreadLocal<Locale> locale = new ThreadLocal<>();

    @Override
    public void setRoot(String root) {
        this.root = root;
        map.clear();

        if (logger.isInfoEnable())
            logger.info("设置运行期根路径：{}", root);
    }

    @Override
    public String getAbsolutePath(String path) {
        String absolutePath = map.get(path);
        if (absolutePath == null) {
            if (path.startsWith("abs:"))
                absolutePath = path.substring(4);
            else
                absolutePath = new File(root + "/" + path).getAbsolutePath();
            map.put(path, absolutePath);
        }

        return absolutePath;
    }

    @Override
    public void setLocale(Locale locale) {
        this.locale.set(locale);
    }

    @Override
    public Locale getLocale() {
        Locale locale = this.locale.get();
        if (locale == null)
            locale = Locale.getDefault();

        return locale;
    }
}
