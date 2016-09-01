package org.lpw.tephra.script;

import org.lpw.tephra.ctrl.context.Request;
import org.lpw.tephra.ctrl.execute.Execute;
import org.lpw.tephra.ctrl.template.Templates;
import org.lpw.tephra.ctrl.validate.Validate;
import org.lpw.tephra.ctrl.validate.Validators;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lpw
 */
@Controller("tephra.script.js.ctrl")
@Execute(name = "/tephra/script/", code = "99")
public class JsCtrl {
    private static final String METHOD = "method";

    @Autowired
    protected Request request;
    @Autowired
    protected Templates templates;
    @Autowired
    protected Engine engine;

    @Execute(name = "js", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = METHOD, failureCode = 1, failureArgKeys = {"tephra.script.method"}),
            @Validate(validator = ScriptService.VALIDATOR_EXISTS_METHOD, parameter = METHOD, failureCode = 2)})
    public Object execute() {
        return engine.execute(request.get(METHOD));
    }

    @Execute(name = "debug", type = Templates.FREEMARKER, template = "debug")
    public Object debug() {
        Map<String, Object> map = new HashMap<>();
        map.put("names", engine.names());

        return map;
    }

    @Execute(name = "debug/.+", type = Templates.STRING, validates = {
            @Validate(validator = ScriptValidator.NAME, parameter = "parameter", string = {"validator1", "validator2"})
    })
    public Object debugScript() {
        templates.get(Templates.STREAM).setContentType("text/javascript");
        String uri = request.getUri();

        return engine.read(uri.substring(uri.lastIndexOf('/')));
    }
}
