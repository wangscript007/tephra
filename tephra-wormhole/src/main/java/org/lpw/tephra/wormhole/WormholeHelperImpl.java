package org.lpw.tephra.wormhole;

import org.lpw.tephra.bean.ContextRefreshedListener;
import org.lpw.tephra.crypto.Sign;
import org.lpw.tephra.util.Context;
import org.lpw.tephra.util.Converter;
import org.lpw.tephra.util.Generator;
import org.lpw.tephra.util.Http;
import org.lpw.tephra.util.Io;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lpw
 */
@Service("tephra.wormhole.helper")
public class WormholeHelperImpl implements WormholeHelper, ContextRefreshedListener {
    @Inject
    private Validator validator;
    @Inject
    private Context context;
    @Inject
    private Generator generator;
    @Inject
    private Converter converter;
    @Inject
    private Io io;
    @Inject
    private Sign sign;
    @Inject
    private Http http;
    @Inject
    private Logger logger;
    @Value("${tephra.wormhole.root:}")
    private String root;
    @Value("${tephra.wormhole.image:}")
    private String image;
    @Value("${tephra.wormhole.file:}")
    private String file;
    @Value("${tephra.wormhole.hosts:}")
    private String hosts;
    private String[] hostArray;

    @Override
    public boolean isImageUri(String uri) {
        return uri.startsWith("/whimg/");
    }

    @Override
    public boolean isFileUri(String uri) {
        return uri.startsWith("/whfile/");
    }

    @Override
    public String getUrl(String uri) {
        return root + uri;
    }

    @Override
    public String getWsUrl() {
        if (validator.isEmpty(hostArray))
            return "";

        return "ws://" + hostArray[generator.random(0, hostArray.length - 1)] + "/whws";
    }

    @Override
    public String getWssUrl() {
        if (validator.isEmpty(hostArray))
            return "";

        return "wss://" + hostArray[generator.random(0, hostArray.length - 1)] + "/whws";
    }

    @Override
    public boolean auth(AuthType type, String token, String ticket) {
        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        map.put("ticket", ticket);

        return "success".equals(post("/whauth/" + type.getName(), null, map));
    }

    @Override
    public String post(String uri, Map<String, String> requestHeaders, Map<String, String> parameters) {
        return http.post(root + uri, null, parameters);
    }

    @Override
    public String post(String uri, Map<String, String> requestHeaders, String parameters) {
        return http.post(root + uri, null, parameters);
    }

    @Override
    public String image(String path, String name, String suffix, String sign, InputStream inputStream) {
        return save(image, path, name, suffix, sign, inputStream);
    }

    @Override
    public String image(String path, String name, String sign, File file) {
        return save(image, path, name, sign, file);
    }

    @Override
    public String file(String path, String name, String suffix, String sign, InputStream inputStream) {
        return save(this.file, path, name, suffix, sign, inputStream);
    }

    @Override
    public String file(String path, String name, String sign, File file) {
        return save(this.file, path, name, sign, file);
    }

    private String save(String url, String path, String name, String suffix, String sign, InputStream inputStream) {
        if (validator.isEmpty(url))
            return null;

        File file = new File(context.getAbsoluteRoot() + getFilename(name, suffix));
        io.write(file.getAbsolutePath(), inputStream);
        String whUrl = save(url, path, name, sign, file);
        io.delete(file);

        return whUrl;
    }

    private String save(String url, String path, String name, String sign, File file) {
        if (validator.isEmpty(url))
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

        return http.upload(url, null, parameters, files);
    }

    private String getFilename(String name, String suffix) {
        int indexOf;
        if (!validator.isEmpty(name) && (indexOf = name.lastIndexOf('.')) > -1)
            suffix = name.substring(indexOf);

        if (!validator.isEmpty(suffix))
            return generator.random(32) + suffix;

        return generator.random(32);
    }

    @Override
    public void download(String uri, String file) {
        try (OutputStream outputStream = new FileOutputStream(file)) {
            http.get(getUrl(uri), null, null, null, outputStream);
        } catch (Throwable throwable) {
            logger.warn(throwable, "下载Wormhole文件[{}:{}]时发生异常！", uri, file);
        }
    }

    @Override
    public int getContextRefreshedSort() {
        return 9;
    }

    @Override
    public void onContextRefreshed() {
        if (validator.isEmpty(root))
            return;

        if (validator.isEmpty(image))
            image = root + "/whimg/save";
        if (validator.isEmpty(file))
            file = root + "/whfile/save";
        hostArray = converter.toArray(hosts, ",");
    }
}
