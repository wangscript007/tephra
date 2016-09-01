package org.lpw.tephra.workbench.front;

import org.lpw.tephra.ctrl.context.Request;
import org.lpw.tephra.ctrl.execute.Execute;
import org.lpw.tephra.ctrl.template.Templates;
import org.lpw.tephra.ctrl.validate.Validate;
import org.lpw.tephra.ctrl.validate.Validators;
import org.lpw.tephra.util.Message;
import org.lpw.tephra.workbench.Suffix;
import org.lpw.tephra.workbench.ui.UiHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lpw
 */
@Controller("tephra.workbench.front" + Suffix.CTRL)
public class FrontCtrl {
    @Autowired
    protected Message message;
    @Autowired
    protected Request request;
    @Autowired
    protected UiHelper uiHelper;

    @Execute(name = "/", type = Templates.FREEMARKER, template = "/tephra/workbench/front/index")
    public Object index() {
        Map<String, Object> map = new HashMap<>();
        map.put("menus", uiHelper.getMenus());

        return map;
    }

    @Execute(name = "/message", validates = {@Validate(validator = Validators.NOT_EMPTY, parameter = "key", failureCode = 9901, failureArgKeys = {"tephra.workbench.front.message.key"})})
    public Object message() {
        return message.get(request.get("key"));
    }
}
