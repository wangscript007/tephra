package org.lpw.tephra.ctrl.template;

import net.sf.json.JSONObject;
import org.lpw.tephra.ctrl.Failure;
import org.lpw.tephra.util.Message;
import org.lpw.tephra.util.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * 模板支持类。
 *
 * @author lpw
 */
public abstract class TemplateSupport implements Template {
    @Autowired
    protected Validator validator;
    @Autowired
    protected Message message;
    @Value("${tephra.ctrl.dispatcher.not-permit:9997}")
    protected int notPermit;
    @Value("${tephra.ctrl.dispatcher.busy:9998}")
    protected int busy;
    @Value("${tephra.ctrl.dispatcher.exception:9999}")
    protected int exception;
    protected ThreadLocal<String> contentType = new ThreadLocal<>();

    @Override
    public String getContentType() {
        String contentType = this.contentType.get();

        return validator.isEmpty(contentType) ? getDefaultContentType() : contentType;
    }

    @Override
    public void setContentType(String contentType) {
        this.contentType.set(contentType);
    }

    /**
     * 获取默认请求输出内容类型。
     *
     * @return 请求输出内容类型。
     */
    protected abstract String getDefaultContentType();

    @Override
    public Object failure(int code, String message, String parameter, String value) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("message", message);
        JSONObject object = new JSONObject();
        object.put("name", parameter);
        object.put("value", value);
        json.put("parameter", object);

        return json;
    }

    @Override
    public Object success(Object data, String key, Object... args) {
        JSONObject object = new JSONObject();
        object.put("code", 0);
        if (data != null)
            object.put("data", data);
        if (!validator.isEmpty(key))
            object.put("message", message.get(key, args));

        return object;
    }

    protected JSONObject getFailure(Failure failure) {
        JSONObject object = new JSONObject();
        object.put("code", getFailureCode(failure));
        object.put("message", message.get(failure.getMessageKey()));

        return object;
    }

    protected int getFailureCode(Failure failure) {
        if (failure == Failure.NotPermit)
            return notPermit;

        else if (failure == Failure.Busy)
            return busy;

        return exception;
    }
}
