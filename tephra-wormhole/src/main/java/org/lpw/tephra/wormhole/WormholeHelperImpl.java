package org.lpw.tephra.wormhole;

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
    private Http http;
    @Value("${tephra.wormhole.image.url:}")
    private String imageUrl;

    @Override
    public String saveImage(String path, String name, File file) {
        if (validator.isEmpty(imageUrl))
            return null;

        Map<String, String> headers = new HashMap<>();
        if (!validator.isEmpty(path))
            headers.put("path", path);
        if (!validator.isEmpty(name))
            headers.put("name", name);
        Map<String, File> files = new HashMap<>();
        files.put("file", file);

        return http.upload(imageUrl, headers, null, files);
    }
}
