package org.lpw.tephra.script;

import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author lpw
 */
@Service("tephra.script.service")
public class ScriptServiceImpl implements ScriptService {
    private static final String METHOD = "tephra.validate";
    @Autowired
    protected Engine engine;
    @Autowired
    protected Arguments arguments;

    @Override
    public JSONObject validate(String[] names, String parameter) {
        arguments.set("parameter", parameter);
        arguments.set("names", names);

        return JSONObject.fromObject(engine.execute(METHOD));
    }
}
