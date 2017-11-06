package org.lpw.tephra.chrome;

import com.alibaba.fastjson.JSONObject;
import org.lpw.tephra.util.Converter;
import org.lpw.tephra.util.Http;
import org.lpw.tephra.util.Json;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Thread;
import org.lpw.tephra.util.TimeUnit;
import org.lpw.tephra.ws.WsClient;
import org.lpw.tephra.ws.WsClientListener;
import org.lpw.tephra.ws.WsClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * @author lpw
 */
@Component("tephra.chrome.client")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ChromeClientImpl implements WsClientListener, ChromeClient {
    @Inject
    private Http http;
    @Inject
    private Json json;
    @Inject
    private Thread thread;
    @Inject
    private Converter converter;
    @Inject
    private Logger logger;
    @Inject
    private WsClients wsClients;
    @Value("${tephra.chrome.max-wait:30}")
    private int maxWait;
    private WsClient wsClient;
    private String service;
    private String url;
    private int wait;
    private JSONObject message;
    private List<String> list;
    private long time;

    @Override
    public ChromeClient set(String service, String url, int wait, JSONObject message) {
        this.service = service;
        this.url = url;
        this.wait = wait;
        this.message = message;

        return this;
    }

    @Override
    public byte[] call() throws Exception {
        JSONObject object = json.toObject(http.get("http://" + service + "/json/new", null, url));
        thread.sleep(wait, TimeUnit.Second);
        wsClient = wsClients.get();
        try {
            list = new ArrayList<>();
            wsClient.connect(this, object.getString("webSocketDebuggerUrl"));
            for (int i = 0; i < maxWait; i++) {
                if (!list.isEmpty() && System.currentTimeMillis() - time > 1000L)
                    break;

                thread.sleep(1, TimeUnit.Second);
            }
            if (list.isEmpty()) {
                logger.warn(null, "请求[{}]等待[{}]秒未获得Chrome推送的数据！", message, maxWait);

                return null;
            }
        } catch (Throwable throwable) {
            logger.warn(throwable, "请求Chrome[{}]时发生异常！", object.toJSONString());
        } finally {
            wsClient.close();
            http.get("http://" + service + "/json/close/" + object.getString("id"), null, "");
        }

        String result = getResult();
        if (logger.isDebugEnable())
            logger.debug("接收到Chrome推送的数据[{}:{}]。", list.size(), converter.toBitSize(result.length()));
        list.clear();
        JSONObject obj = json.toObject(result);
        if (obj == null)
            return null;

        if (!obj.containsKey("result")) {
            logger.warn(null, "请求Chrome失败[{}]！", result);

            return null;
        }

        return Base64.getDecoder().decode(obj.getJSONObject("result").getString("data"));
    }

    private String getResult() {
        if (list.size() == 1)
            return list.get(0);

        String[] array = new String[list.size()];
        int i = 1;
        for (String string : list) {
            if (string.charAt(0) == '{')
                array[0] = string;
            else if (string.charAt(string.length() - 1) == '}')
                array[array.length - 1] = string;
            else
                array[i++] = string;
        }

        StringBuilder sb = new StringBuilder();
        for (String string : array)
            sb.append(string);

        return sb.toString();
    }

    @Override
    public void connect() {
        wsClient.send(message.toJSONString());
    }

    @Override
    public void receive(String message) {
        time = System.currentTimeMillis();
        list.add(message);
    }

    @Override
    public void disconnect() {
    }
}
