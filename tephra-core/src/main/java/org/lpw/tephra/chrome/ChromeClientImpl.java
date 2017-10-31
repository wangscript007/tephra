package org.lpw.tephra.chrome;

import com.alibaba.fastjson.JSONObject;
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
import java.util.Base64;

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
    private String result;

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
        result = null;
        wsClient = wsClients.get();
        try {
            wsClient.connect(this, object.getString("webSocketDebuggerUrl"));
            for (int i = 0; i < maxWait; i++) {
                if (result != null)
                    break;

                thread.sleep(1, TimeUnit.Second);
            }
            if (result == null) {
                logger.warn(null, "请求[{}]等待[{}]秒未获得Chrome推送的数据！", message, maxWait);

                return null;
            }
        } catch (Throwable throwable) {
            logger.warn(throwable, "请求Chrome[{}]时发生异常！", object.toJSONString());
        } finally {
            wsClient.close();
            http.get("http://" + service + "/json/close/" + object.getString("id"), null, "");
        }

        JSONObject obj = json.toObject(result);
        if (obj == null)
            return null;

        if (!obj.containsKey("result")) {
            logger.warn(null, "请求Chrome失败[{}]！", result);

            return null;
        }

        return Base64.getDecoder().decode(obj.getJSONObject("result").getString("data"));
    }

    @Override
    public void connect() {
        wsClient.send(message.toJSONString());
    }

    @Override
    public void receive(String message) {
        result = message;
        if (logger.isDebugEnable())
            logger.debug("接收到Chrome推送的数据[{}]。", result.length() < 8192 ? result : result.length());
    }

    @Override
    public void disconnect() {
    }
}
