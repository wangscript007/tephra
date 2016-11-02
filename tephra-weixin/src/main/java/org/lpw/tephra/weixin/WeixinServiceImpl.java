package org.lpw.tephra.weixin;

import net.sf.json.JSONObject;
import org.lpw.tephra.bean.ContextClosedListener;
import org.lpw.tephra.bean.ContextRefreshedListener;
import org.lpw.tephra.cache.Cache;
import org.lpw.tephra.crypto.Digest;
import org.lpw.tephra.ctrl.context.Session;
import org.lpw.tephra.dao.Commitable;
import org.lpw.tephra.util.Context;
import org.lpw.tephra.util.Converter;
import org.lpw.tephra.util.Generator;
import org.lpw.tephra.util.Http;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Validator;
import org.lpw.tephra.util.Xml;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author lpw
 */
@Service("tephra.weixin.service")
public class WeixinServiceImpl implements WeixinService, ContextRefreshedListener, ContextClosedListener {
    private static final String SESSION_OPEN_ID = "tephra.weixin.service.open-id";
    private static final String CACHE_NICKNAME = "tephra.weixin.service.nickname:";

    @Autowired
    protected Digest digest;
    @Autowired
    protected Validator validator;
    @Autowired
    protected Converter converter;
    @Autowired
    protected Http http;
    @Autowired
    protected Context context;
    @Autowired
    protected Generator generator;
    @Autowired
    protected Cache cache;
    @Autowired
    protected Logger logger;
    @Autowired
    protected Xml xml;
    @Autowired
    protected Set<Commitable> commitables;
    @Autowired
    protected Session session;
    @Autowired
    protected WeixinHelper weixinHelper;
    @Autowired(required = false)
    protected WeixinListener weixinListener;
    @Value("${tephra.weixin.mp.thread:5}")
    protected int thread;
    protected ExecutorService executorService;

    @Override
    public boolean echo(String appId, String signature, String timestamp, String nonce) {
        List<String> list = new ArrayList<>();
        list.add(weixinHelper.getConfig(appId).getToken());
        list.add(timestamp);
        list.add(nonce);
        Collections.sort(list);
        StringBuilder sb = new StringBuilder();
        list.forEach(sb::append);
        boolean success = digest.sha1(sb.toString()).equals(signature);

        if (logger.isDebugEnable())
            logger.debug("验证服务器[{}:signature={};timestamp={};nonce={}]。", success, signature, timestamp, nonce);

        return success;
    }

    @Override
    public void redirect(String appId, String code) {
        Map<String, String> map = new HashMap<>();
        map.put("appid", appId);
        map.put("secret", weixinHelper.getConfig(appId).getSecret());
        map.put("code", code);
        map.put("grant_type", "authorization_code");
        JSONObject json = JSONObject.fromObject(http.get("https://api.weixin.qq.com/sns/oauth2/access_token", null, map));
        if (!json.has("openid"))
            return;

        String openId = json.getString("openid");
        session.set(SESSION_OPEN_ID, openId);
        if (!json.has("access_token") || getNickname(openId) != null)
            return;

        map.clear();
        map.put("access_token", json.getString("access_token"));
        map.put("openid", openId);
        map.put("lang", "zh_CN");
        json = JSONObject.fromObject(http.get("https://api.weixin.qq.com/sns/userinfo", null, map));
        if (!json.has("nickname"))
            return;

        cache.put(CACHE_NICKNAME + openId, json.getString("nickname"), false);
    }

    @Override
    public String xml(String appId, String xml) {
        if (logger.isDebugEnable())
            logger.debug("收到微信消息\n{}", xml);

        if (weixinListener != null) {
            executorService.submit(() -> {
                Map<String, String> map = this.xml.toMap(xml, false);
//                String mpId = map.get("ToUserName");
                String userOpenId = map.get("FromUserName");
                Timestamp time = new Timestamp(converter.toLong(map.get("CreateTime")) * 1000);
                String type = map.get("MsgType");
                String messageId = map.get("MsgId");
                xml(map, appId, userOpenId, time, type, messageId);
                commitables.forEach(Commitable::close);
            });
        }

        return "";
    }

    protected void xml(Map<String, String> map, String appId, String userOpenId, Timestamp time, String type, String messageId) {
        if ("text".equals(type)) {
            weixinListener.text(appId, userOpenId, messageId, map.get("Content"), time);

            return;
        }

        if ("image".equals(type)) {
            weixinListener.image(appId, userOpenId, messageId, weixinHelper.download(appId, map.get("MediaId"), time, true), time);

            return;
        }

        if ("voice".equals(type)) {
            weixinListener.voice(appId, userOpenId, messageId, map.get("format"), weixinHelper.download(appId, map.get("MediaId"), time, true), time);

            return;
        }

        if ("video".equals(type) || "shortvideo".equals(type)) {
            weixinListener.video(appId, userOpenId, messageId, weixinHelper.download(appId, map.get("MediaId"), time, false),
                    weixinHelper.download(appId, map.get("ThumbMediaId"), time, true), time);

            return;
        }

        if ("location".equals(type)) {
            weixinListener.location(appId, userOpenId, messageId, converter.toDouble(map.get("Location_X")), converter.toDouble(map.get("Location_Y")),
                    converter.toInt(map.get("Scale")), map.get("Label"), time);

            return;
        }

        if ("event".equals(type)) {
            String event = map.get("Event");
            String key = map.get("EventKey");
            String ticket = map.get("Ticket");
            if ("subscribe".equals(event)) {
                if (!validator.isEmpty(key))
                    key = key.substring(8);
                weixinListener.subscribe(appId, userOpenId, key, ticket, time);

                return;
            }

            if ("SCAN".equals(event)) {
                weixinListener.scan(appId, userOpenId, key, ticket, time);

                return;
            }

            if ("CLICK".equals(event)) {
                weixinListener.click(appId, userOpenId, key, time);

                return;
            }

            if ("VIEW".equals(event)) {
                weixinListener.redirect(appId, userOpenId, key, time);

                return;
            }

            if ("unsubscribe".equals(event)) {
                weixinListener.unsubscribe(appId, userOpenId, time);

                return;
            }
        }

        logger.warn(null, "未处理微信通知[{}]。", converter.toString(map));
    }

    @Override
    public String pay(Map<String, String> parameters) {
        return null;
    }

    @Override
    public String getOpenId() {
        return session.get(SESSION_OPEN_ID);
    }

    @Override
    public String getNickname(String openId) {
        return cache.get(CACHE_NICKNAME + openId);
    }

    @Override
    public int getContextRefreshedSort() {
        return 21;
    }

    @Override
    public void onContextRefreshed() {
        if (weixinListener != null)
            executorService = Executors.newFixedThreadPool(thread);
    }

    @Override
    public int getContextClosedSort() {
        return 21;
    }

    @Override
    public void onContextClosed() {
        if (weixinListener != null)
            executorService.shutdownNow();
    }
}
