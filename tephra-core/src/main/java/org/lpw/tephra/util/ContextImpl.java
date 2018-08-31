package org.lpw.tephra.util;

import org.lpw.tephra.atomic.Closable;
import org.lpw.tephra.bean.ContextRefreshedListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lpw
 */
@Component("tephra.util.context")
public class ContextImpl implements Context, Closable, ContextRefreshedListener {
    @Inject
    private Validator validator;
    @Inject
    private Thread thread;
    @Inject
    private Logger logger;
    @Value("${tephra.util.context.charset:UTF-8}")
    private String charset;
    private String root;
    private Map<String, String> absolutePath = new ConcurrentHashMap<>();
    private ThreadLocal<Locale> locale = new ThreadLocal<>();
    private ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal<>();

    @Override
    public String getAbsoluteRoot() {
        return root;
    }

    @Override
    public String getAbsolutePath(String path) {
        String absolutePath = this.absolutePath.get(path);
        if (absolutePath == null) {
            if (path.startsWith("abs:"))
                absolutePath = path.substring(4);
            else if (path.startsWith("classpath:")) {
                URL url = getClass().getClassLoader().getResource(path.substring(10));
                if (url == null)
                    return null;

                absolutePath = url.getPath();
            } else
                absolutePath = new File(root + "/" + path).getAbsolutePath();
            this.absolutePath.put(path, absolutePath);
        }

        return absolutePath;
    }

    @Override
    public String getCharset(String charset) {
        return validator.isEmpty(charset) ? this.charset : charset;
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

    @Override
    public void clearThreadLocal() {
        getThreadLocalMap().clear();
    }

    @Override
    public void putThreadLocal(String key, Object value) {
        getThreadLocalMap().put(key, value);
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public <T> T getThreadLocal(String key) {
        return (T) getThreadLocalMap().get(key);
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public <T> T removeThreadLocal(String key) {
        return (T) getThreadLocalMap().remove(key);
    }

    @Override
    public void close() {
        getThreadLocalMap().clear();
    }

    private Map<String, Object> getThreadLocalMap() {
        Map<String, Object> map = threadLocal.get();
        if (map == null) {
            map = new HashMap<>();
            threadLocal.set(map);
        }

        return map;
    }

    @Override
    public int getContextRefreshedSort() {
        return 1;
    }

    @Override
    public void onContextRefreshed() {
        String path = getClass().getResource("/").getPath();
        for (String name : new String[]{"/classes/", "/WEB-INF/"}) {
            int indexOf = path.lastIndexOf(name);
            if (indexOf > -1)
                path = path.substring(0, indexOf + 1);
        }

        root = path.replace(File.separatorChar, '/');
        absolutePath.clear();

        if (logger.isInfoEnable())
            logger.info("设置运行期根路径：{}", root);
    }
}
