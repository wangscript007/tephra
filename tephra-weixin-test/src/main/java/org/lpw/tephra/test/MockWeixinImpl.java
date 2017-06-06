package org.lpw.tephra.test;

import com.alibaba.fastjson.JSONObject;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lpw
 */
@Aspect
@Component("tephra.test.mock.weixin")
public class MockWeixinImpl implements MockWeixin {
    private String openId;
    private String nickname;
    private String portrait;
    private List<Object[]> args = new ArrayList<>();
    private JSONObject object;

    @Override
    public void reset() {
        openId = null;
        nickname = null;
        portrait = null;
        args = new ArrayList<>();
        object = null;
    }

    @Override
    public void auth(String openId, String nickname, String portrait) {
        this.openId = openId;
        this.nickname = nickname;
        this.portrait = portrait;
        object = new JSONObject();
        object.put("openid", openId);
        object.put("nickname", nickname);
        object.put("headimgurl", portrait);
    }

    @Around("target(org.lpw.tephra.weixin.WeixinService)")
    public Object service(ProceedingJoinPoint point) throws Throwable {
        args.add(point.getArgs() == null ? new Object[0] : point.getArgs());
        String name = point.getSignature().getName();
        if (name.equals("auth"))
            return object;

        if (name.equals("getOpenId"))
            return openId;

        if (name.equals("getNickname"))
            return nickname;

        if (name.equals("getPortrait"))
            return portrait;

        return point.proceed();
    }

    @Override
    public List<Object[]> getArgs() {
        return args;
    }
}
