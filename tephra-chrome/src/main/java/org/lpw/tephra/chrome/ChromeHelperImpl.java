package org.lpw.tephra.chrome;

import com.alibaba.fastjson.JSONObject;
import org.lpw.tephra.bean.ContextClosedListener;
import org.lpw.tephra.bean.ContextRefreshedListener;
import org.lpw.tephra.chrome.runner.Image;
import org.lpw.tephra.chrome.runner.Pdf;
import org.lpw.tephra.storage.StorageListener;
import org.lpw.tephra.storage.Storages;
import org.lpw.tephra.util.Context;
import org.lpw.tephra.util.Converter;
import org.lpw.tephra.util.Generator;
import org.lpw.tephra.util.Http;
import org.lpw.tephra.util.Io;
import org.lpw.tephra.util.Json;
import org.lpw.tephra.util.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author lpw
 */
@Component("tephra.chrome.helper")
public class ChromeHelperImpl implements ChromeHelper, StorageListener, ContextRefreshedListener, ContextClosedListener {
    @Inject
    private Generator generator;
    @Inject
    private Http http;
    @Inject
    private Json json;
    @Inject
    private Context context;
    @Inject
    private Io io;
    @Inject
    private Converter converter;
    @Inject
    private Logger logger;
    private String command;
    private String pdf;
    private String image;
    @Value("${tephra.chrome.services:/WEB-INF/chrome}")
    private String services;
    @Value("${tephra.chrome.max-thread:5}")
    private int maxThread;
    private ExecutorService executorService;
    private List<String> list;

    @Override
    public String pdf(String url, int wait, int width, int height, String range, String output) {
        return execute(pdf, url, wait, 0, 0, width, height, range, "pdf", 0, output, ".pdf");
    }

    @Override
    public String png(String url, int wait, int x, int y, int width, int height, String output) {
        return execute(image, url, wait, x, y, width, height, null, "png", 0, output, ".png");
    }

    @Override
    public String jpeg(String url, int wait, int x, int y, int width, int height, String output) {
        return execute(image, url, wait, x, y, width, height, null, "jpeg", 100, output, ".jpg");
    }

    private String execute(String type, String url, int wait, int x, int y, int width, int height, String range,
                           String format, int quality, String output, String suffix) {
        Future<String> future = executorService.submit(() -> {
            String service = list.get(generator.random(0, list.size() - 1));
            JSONObject object = json.toObject(http.get("http://" + service + "/json/new", null, url));
            Thread.sleep(wait * 1000);
            String path = context.getAbsolutePath(output);
            io.mkdirs(path);
            String file = path + "/" + generator.random(32) + suffix;
            String[] hp = converter.toArray(service, ":");
            String cmd = command + type + " -host=" + hp[0] + " -port=" + hp[1]
                    + " -uri=" + new URI(object.getString("webSocketDebuggerUrl")).getPath() + " -output=" + file
                    + " -x=" + x + " -y=" + y + " -width=" + width + " -height=" + height
                    + " -range=" + range + " -format=" + format + " -quality=" + quality;
            if (logger.isDebugEnable())
                logger.debug("执行Chrome指令[{}:{}]。", url, cmd);
            Process process = Runtime.getRuntime().exec(cmd);
            if (logger.isDebugEnable())
                logger.debug("Chrome指令[{}:{}]执行输出[{}:{}]。", url, cmd, io.readAsString(process.getInputStream()),
                        io.readAsString(process.getErrorStream()));
            http.get("http://" + service + "/json/close/" + object.getString("id"), null, "");

            return file;
        });

        try {
            return future.get();
        } catch (Exception e) {
            logger.warn(e, "获取Chrome输出文件时发生异常！");

            return null;
        }
    }

    @Override
    public int getContextRefreshedSort() {
        return 9;
    }

    @Override
    public void onContextRefreshed() {
        String path = ChromeHelper.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        command = "java -cp " + getJsonPath(path) + ":" + path + " ";
        pdf = Pdf.class.getName();
        image = Image.class.getName();
        if (logger.isInfoEnable())
            logger.info("设置Chrome headless客户端命令[{}:{}:{}]。", command, pdf, image);

        executorService = Executors.newFixedThreadPool(maxThread);
    }

    private String getJsonPath(String path) {
        File[] files = new File(path).getParentFile().listFiles();
        if (files != null)
            for (File file : files)
                if (file.getName().startsWith("fastjson-"))
                    return file.getAbsolutePath();

        return "";
    }

    @Override
    public int getContextClosedSort() {
        return 9;
    }

    @Override
    public void onContextClosed() {
        executorService.shutdown();
    }

    @Override
    public String getStorageType() {
        return Storages.TYPE_DISK;
    }

    @Override
    public String[] getScanPathes() {
        return new String[]{services};
    }

    @Override
    public void onStorageChanged(String path, String absolutePath) {
        Set<String> set = new HashSet<>();
        for (String string : converter.toArray(io.readAsString(absolutePath), "\n")) {
            string = string.trim();
            if (string.length() == 0 || string.charAt(0) == '#' || string.indexOf(':') == -1)
                continue;

            set.add(string);
        }
        list = new ArrayList<>(set);
        if (logger.isInfoEnable())
            logger.info("更新Chrome调试服务集[{}]。", converter.toString(set));
    }
}
