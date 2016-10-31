package org.lpw.tephra.carousel;

import net.sf.json.JSONObject;
import org.lpw.tephra.util.Http;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @auth lpw
 */
@Component("tephra.carousel.helper")
public class CarouselHelperImpl implements CarouselHelper {
    @Autowired
    protected Validator validator;
    @Autowired
    protected Http http;
    @Autowired
    protected Logger logger;
    @Value("${tephra.carousel.url:http://localhost:8080}")
    protected String url;

    @Override
    public boolean config(String name, String description, ActionBuilder actionBuilder) {
        return config(name, description, actionBuilder, 0, 0, 0, false);
    }

    @Override
    public boolean config(String name, String description, ActionBuilder actionBuilder, int delay, int interval, int times, boolean wait) {
        if (validator.isEmpty(name) || actionBuilder == null)
            return false;

        JSONObject object = new JSONObject();
        object.put("name", name);
        object.put("description", description);
        object.put("actions", actionBuilder.get());
        if (delay > 0)
            object.put("delay", delay);
        if (interval > 0)
            object.put("interval", interval);
        if (times > 0)
            object.put("times", times);
        if (wait)
            object.put("wait", 1);
        try {
            JSONObject json = JSONObject.fromObject(http.post(url + "/config/update", null, object.toString()));
            if (logger.isDebugEnable())
                logger.debug("配置Carousel[{}]返回[{}]。", object, json);

            return json.getInt("code") == 0;
        } catch (Throwable e) {
            logger.warn(e, "配置Carousel[{}]时发生异常！", object);

            return false;
        }
    }

    @Override
    public String execute(String name, int delay, Map<String, String> header, String data) {
        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        if (delay > 0)
            map.put("delay", "" + delay);
        map.put("data", data);

        return http.post(url + "/process/execute", header, map);
    }
}
