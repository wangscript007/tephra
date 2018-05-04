package org.lpw.tephra.wormhole;

import org.lpw.tephra.crypto.Sign;
import org.lpw.tephra.util.Http;
import org.lpw.tephra.util.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lpw
 */
@Service("tephra.wormhole.helper")
public class WormholeHelperImpl implements WormholeHelper {
    @Inject
    private Validator validator;
    @Inject
    private Sign sign;
    @Inject
    private Http http;
    @Value("${tephra.wormhole.url:}")
    private String url;
    @Value("${tephra.wormhole.url.save-image:}")
    private String saveImageUrl;

    @Override
    public String saveImage(String path, String name, String sign, File file) {
        if (validator.isEmpty(url) || validator.isEmpty(saveImageUrl))
            return null;

        Map<String, String> parameters = new HashMap<>();
        if (!validator.isEmpty(path))
            parameters.put("path", path);
        if (!validator.isEmpty(name))
            parameters.put("name", name);
        if (!validator.isEmpty(sign))
            parameters.put("sign-name", sign);
        this.sign.put(parameters, sign);

        Map<String, File> files = new HashMap<>();
        files.put("file", file);

        return url + http.upload(saveImageUrl, null, parameters, files);
    }
}
