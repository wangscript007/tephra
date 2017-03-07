package org.lpw.tephra.ctrl.template.json;

import com.alibaba.fastjson.JSONObject;
import org.lpw.tephra.ctrl.Failure;
import org.lpw.tephra.ctrl.template.Template;
import org.lpw.tephra.ctrl.template.TemplateSupport;
import org.lpw.tephra.ctrl.template.Templates;
import org.lpw.tephra.dao.model.Model;
import org.lpw.tephra.dao.model.ModelHelper;
import org.lpw.tephra.dao.orm.PageList;
import org.lpw.tephra.util.Json;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Message;
import org.lpw.tephra.util.Validator;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author lpw
 */
@Controller("tephra.ctrl.template.json")
public class TemplateImpl extends TemplateSupport implements Template {
    @Inject
    private Validator validator;
    @Inject
    private Message message;
    @Inject
    private Json json;
    @Inject
    private Logger logger;
    @Inject
    private ModelHelper modelHelper;

    @Override
    public String getType() {
        return Templates.JSON;
    }

    @Override
    public String getContentType() {
        return "application/json";
    }

    @Override
    public void process(String name, Object data, OutputStream outputStream) throws IOException {
        if (data instanceof Failure) {
            write(getFailure((Failure) data), outputStream);

            return;
        }

        if (data instanceof Model)
            data = modelHelper.toJson((Model) data);
        else if (data instanceof PageList)
            data = ((PageList<? extends Model>) data).toJson();
        else if (data instanceof String)
            data = json((String) data);

        write(pack(data), outputStream);
    }

    private Object json(String string) {
        if (string.length() == 0)
            return string;

        char ch = string.charAt(0);
        if (ch == '{')
            return json.toObject(string);

        if (ch == '[')
            return json.toArray(string);

        return string;
    }

    private Object pack(Object object) {
        if (object instanceof JSONObject && ((JSONObject) object).containsKey("code"))
            return object;

        JSONObject json = new JSONObject();
        json.put("code", 0);
        json.put("data", object);

        return json;
    }

    private void write(Object data, OutputStream outputStream) throws IOException {
        outputStream.write(data.toString().getBytes("UTF-8"));
    }
}
