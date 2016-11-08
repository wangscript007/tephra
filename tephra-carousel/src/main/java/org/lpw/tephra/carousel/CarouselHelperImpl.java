package org.lpw.tephra.carousel;

import net.sf.json.JSONObject;
import org.lpw.tephra.ctrl.status.Status;
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
    @Autowired
    protected Status status;
    @Value("${tephra.carousel.url:http://localhost:8080}")
    protected String carouselUrl;
    @Value("${tephra.carousel.service-url:http://localhost:8080}")
    protected String serviceUrl;

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

        return code0(http.post(carouselUrl + "/config/update", null, object.toString()));
    }

    @Override
    public String process(String name, int delay, Map<String, String> header, String data) {
        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        if (delay > 0)
            map.put("delay", "" + delay);
        map.put("data", data);

        return http.post(carouselUrl + "/process/execute", header, map);
    }

    @Override
    public boolean register(String key, String service) {
        Map<String, String> map = new HashMap<>();
        map.put("key", key);
        map.put("service", serviceUrl + service);
        map.put("validate", serviceUrl + status.getUri());
        map.put("validate", "^\\{\"code\":0,.*");

        return code0(http.post(carouselUrl + "/discovery/register", null, map));
    }

    @Override
    public String service(String key, Map<String, String> header, Map<String, String> parameter) {
        if (header == null)
            header = new HashMap<>();
        header.put("carousel-ds-key", key);

        return http.post(carouselUrl + "/discovery/execute", header, parameter);
    }

    protected boolean code0(String string) {
        if (validator.isEmpty(string))
            return false;

        try {
            return JSONObject.fromObject(string).getInt("code") == 0;
        } catch (Throwable e) {
            logger.warn(e, "解析JSON数据时发生异常！", string);

            return false;
        }
    }
}
