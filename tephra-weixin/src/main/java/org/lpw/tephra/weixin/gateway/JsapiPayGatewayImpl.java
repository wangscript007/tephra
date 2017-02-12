package org.lpw.tephra.weixin.gateway;

import com.alibaba.fastjson.JSONObject;
import org.lpw.tephra.util.Json;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lpw
 */
@Service("tephra.weixin.gateway.jsapi")
public class JsapiPayGatewayImpl extends PayGatewaySupport {
    @Inject
    private Json json;

    @Override
    public String getType() {
        return JSAPI;
    }

    @Override
    protected void unifiedorder(Map<String, String> map, String openId) {
        map.put("device_info", "WEB");
        map.put("openid", openId);
    }

    @Override
    protected JSONObject prepay(String mpId, String timestamp, String nonce, String prepay) {
        JSONObject json = new JSONObject();
        json.put("appId", mpId);
        json.put("timeStamp", timestamp);
        json.put("nonceStr", nonce);
        json.put("package", "prepay_id=" + prepay);
        json.put("signType", "MD5");
        Map<String, String> map = new HashMap<>();
        json.forEach((key, value) -> map.put(key, value.toString()));
        json.put("paySign", sign(map, weixinHelper.getConfig(mpId).getMchKey()));

        return json;
    }
}
