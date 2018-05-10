package org.lpw.tephra.wormhole;

import org.lpw.tephra.crypto.Sign;
import org.lpw.tephra.util.Context;
import org.lpw.tephra.util.Generator;
import org.lpw.tephra.util.Http;
import org.lpw.tephra.util.Io;
import org.lpw.tephra.util.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.File;
import java.io.InputStream;
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
    private Context context;
    @Inject
    private Generator generator;
    @Inject
    private Io io;
    @Inject
    private Sign sign;
    @Inject
    private Http http;
    @Value("${tephra.wormhole.url:}")
    private String url;
    @Value("${tephra.wormhole.url.save-image:}")
    private String saveImageUrl;

    @Override
    public boolean enable() {
        return !validator.isEmpty(url) && !validator.isEmpty(saveImageUrl);
    }

    @Override
    public String saveImage(String path, String name, String conentType, String sign, InputStream inputStream) {
        String suffix = null;
        if (!validator.isEmpty(name))
            suffix = name.substring(name.lastIndexOf('.'));
        else if (!validator.isEmpty(conentType))
            suffix = "." + conentType.substring(conentType.lastIndexOf('/') + 1);
        File file = new File(context.getAbsoluteRoot() + generator.random(32) + (suffix == null ? "" : suffix));
        io.write(file.getAbsolutePath(), inputStream);
        String url = saveImage(path, name, sign, file);
        io.delete(file);

        return url;
    }

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
