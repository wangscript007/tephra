package org.lpw.tephra.crypto;

import org.lpw.tephra.storage.StorageListener;
import org.lpw.tephra.util.Converter;
import org.lpw.tephra.util.Io;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lpw
 */
@Component("tephra.crypto.sign")
public class SignImpl implements Sign, StorageListener {
    private static final String SIGN = "sign";
    private static final String SIGN_TIME = "sign-time";

    @Autowired
    protected Converter converter;
    @Autowired
    protected Validator validator;
    @Autowired
    protected Io io;
    @Autowired
    protected Logger logger;
    @Autowired
    protected Digest digest;
    @Value("${tephra.crypto.sign.path:/WEB-INF/sign}")
    protected String path;
    @Value("${tephra.crypto.sign.time:10000}")
    protected long time;
    protected Map<String, String> map = new HashMap<>();

    @Override
    public void put(Map<String, String> map, String name) {
        map.put(SIGN_TIME, converter.toString(System.currentTimeMillis(), "0"));
        map.put(SIGN, get(map, name));
    }

    @Override
    public boolean verify(Map<String, String> map, String name) {
        if (map.get(SIGN) == null)
            return false;

        return System.currentTimeMillis() - converter.toLong(map.get(SIGN_TIME)) < time && get(map, name).equals(map.get(SIGN));
    }

    protected String get(Map<String, String> map, String name) {
        List<String> list = new ArrayList<>(map.keySet());
        list.remove(SIGN);
        Collections.sort(list);

        StringBuilder sb = new StringBuilder();
        String key = getKey(name);
        list.forEach(k -> sb.append(key).append('=').append(map.get(k)).append('&'));
        sb.append(key);

        return digest.md5(sb.toString());
    }

    protected String getKey(String name) {
        return map.get(validator.isEmpty(name) ? "" : name);
    }

    @Override
    public String getStorageType() {
        return "disk";
    }

    @Override
    public String[] getScanPathes() {
        return new String[]{path};
    }

    @Override
    public void onStorageChanged(String path, String absolutePath) {
        Map<String, String> map = new HashMap<>();
        for (String string : converter.toArray(new String(io.read(absolutePath)), "\n")) {
            string = string.trim();
            int indexOf;
            if (string.equals("") || string.startsWith("#") || (indexOf = string.indexOf('=')) == -1)
                continue;

            map.put(string.substring(0, indexOf).trim(), string.substring(indexOf + 1).trim());
        }
        this.map = map;

        if (logger.isInfoEnable())
            logger.info("更新签名密钥[{}]。", converter.toString(map.keySet()));
    }
}
