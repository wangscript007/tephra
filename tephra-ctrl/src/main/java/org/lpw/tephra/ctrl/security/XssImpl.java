package org.lpw.tephra.ctrl.security;

import org.lpw.tephra.storage.StorageListener;
import org.lpw.tephra.storage.Storages;
import org.lpw.tephra.util.Converter;
import org.lpw.tephra.util.Io;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author lpw
 */
@Controller("tephra.ctrl.security.xss")
public class XssImpl implements Xss, StorageListener {
    private static final Pattern SCRIPT = Pattern.compile("<\\s*[sS]\\s*[cC]\\s*[rR]\\s*[iI]\\s*[pP]\\s*[tT].*>");

    @Inject
    private Converter converter;
    @Inject
    private Io io;
    @Inject
    private Validator validator;
    @Inject
    private Logger logger;
    @Value("${tephra.ctrl.security.xss.enable:true}")
    private boolean enable;
    @Value("${tephra.ctrl.security.xss.ignore:/WEB-INF/security/xss}")
    private String ignore;
    private Set<String> ignores = new HashSet<>();

    @Override
    public boolean contains(String uri, Map<String, String> map) {
        if (!enable || ignores.contains(uri))
            return false;

        for (String key : map.keySet()) {
            String value = map.get(key);
            if (validator.isEmpty(value))
                continue;

            if (value.indexOf('<') < value.lastIndexOf('>') && SCRIPT.matcher(value).find()) {
                logger.warn(null, "疑似提交跨站脚本参数[{}:{}={}]！", uri, key, value);

                return true;
            }
        }

        return false;
    }

    @Override
    public String getStorageType() {
        return Storages.TYPE_DISK;
    }

    @Override
    public String[] getScanPathes() {
        return new String[]{ignore};
    }

    @Override
    public void onStorageChanged(String path, String absolutePath) {
        Set<String> set = new HashSet<>();
        for (String string : converter.toArray(io.readAsString(absolutePath), "\n")) {
            if (validator.isEmpty(string))
                continue;

            string = string.trim();
            if (string.charAt(0) == '#')
                continue;

            set.add(string);
        }
        ignores = set;
    }
}
