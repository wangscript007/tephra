package org.lpw.tephra.weixin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.tephra.util.Http;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * @author lpw
 */
@Service("tephra.weixin.sender")
public class WeixinSenderImpl implements WeixinSender {
    @Inject
    private Validator validator;
    @Inject
    private Http http;
    @Inject
    private Logger logger;
    @Inject
    private WeixinHelper weixinHelper;

    @Override
    public boolean sendText(String appId, String receiver, String text) {
        if (validator.isEmpty(receiver) || validator.isEmpty(text))
            return false;

        JSONObject object = new JSONObject();
        object.put("content", text);

        return send(appId, receiver, "text", object);
    }

    @Override
    public boolean sendLink(String appId, String receiver, String uri, String text) {
        return sendText(appId, receiver, "<a href='" + weixinHelper.getRedirectUrl(appId, uri) + "'>" + text + "</a>");
    }

    @Override
    public boolean sendImage(String appId, String receiver, String uri) {
        return send(appId, receiver, "image", uri);
    }

    @Override
    public boolean sendVoice(String appId, String receiver, String uri) {
        return send(appId, receiver, "voice", uri);
    }

    private boolean send(String appId, String receiver, String type, String uri) {
        if (validator.isEmpty(receiver) || validator.isEmpty(uri))
            return false;


        String mediaId = weixinHelper.upload(appId, type, uri);
        if (validator.isEmpty(mediaId))
            return false;

        JSONObject object = new JSONObject();
        object.put("media_id", mediaId);

        return send(appId, receiver, type, object);
    }

    @Override
    public boolean sendVideo(String appId, String receiver, String uri, String thumbnail, String title, String description) {
        if (validator.isEmpty(receiver) || validator.isEmpty(uri) || validator.isEmpty(thumbnail))
            return false;

        String thumbnailMediaId = weixinHelper.upload(appId, "thumb", thumbnail);
        if (validator.isEmpty(thumbnailMediaId))
            return false;

        String videoMediaId = weixinHelper.upload(appId, "video", uri);
        if (validator.isEmpty(videoMediaId))
            return false;

        JSONObject object = new JSONObject();
        object.put("media_id", videoMediaId);
        object.put("thumb_media_id", thumbnailMediaId);
        object.put("title", title);
        object.put("description", description);

        return send(appId, receiver, "video", object);
    }

    @Override
    public boolean sendNews(String appId, String receiver, NewsBuilder builder) {
        if (validator.isEmpty(receiver))
            return false;

        JSONArray array = builder.getArray();
        if (array.isEmpty())
            return false;

        JSONObject object = new JSONObject();
        object.put("articles", array);

        return send(appId, receiver, "news", object);
    }

    private boolean send(String appId, String receiver, String type, JSONObject object) {
        JSONObject json = new JSONObject();
        json.put("touser", receiver);
        json.put("msgtype", type);
        json.put(type, object);
        JSONObject result = JSON.parseObject(http.post("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + weixinHelper.getToken(appId), null, json.toString()));

        if (logger.isDebugEnable())
            logger.debug("发送[{}]消息[{}]到微信服务器[{}]。", type, json, result);

        return result.getIntValue("errcode") == 0;
    }
}
