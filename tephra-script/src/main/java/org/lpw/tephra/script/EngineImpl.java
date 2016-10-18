package org.lpw.tephra.script;

import org.lpw.tephra.util.Context;
import org.lpw.tephra.util.Converter;
import org.lpw.tephra.util.Io;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lpw
 */
@Service("tephra.script.engine")
public class EngineImpl implements Engine {
    private static final String METHOD = "();";
    private static final byte[] READY = "tephra.ready.execute();".getBytes();
    private static final String HAS_METHOD_NAME = "tephra.existsMethod";

    @Autowired
    protected Context context;
    @Autowired
    protected Io io;
    @Autowired
    protected Validator validator;
    @Autowired
    protected Converter converter;
    @Autowired
    protected Logger logger;
    @Value("${tephra.script.path:/WEB-INF/script}")
    protected String path;
    protected ScriptEngineManager manager;
    protected ScriptEngine engine;

    @Override
    public synchronized void refresh() {
        if (manager == null)
            manager = new ScriptEngineManager();

        try {
            ScriptEngine engine = manager.getEngineByName("js");
            engine.eval(read());
            this.engine = engine;
        } catch (ScriptException | IOException e) {
            logger.warn(e, "重新载入脚本时发生异常！");
        }
    }

    protected String read() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        String root = context.getAbsolutePath(path);
        for (String name : names()) {
            InputStream input = new FileInputStream(root + name);
            io.copy(input, output);
            input.close();
        }
        output.write(READY);
        output.close();

        return output.toString();
    }

    @Override
    public boolean existsMethod() {
        return (boolean) execute(HAS_METHOD_NAME);
    }

    @Override
    public Object execute(String name) {
        if (logger.isDebugEnable())
            logger.debug("开始执行脚本服务[{}]。。。", name);

        try {
            return get().eval(name + METHOD);
        } catch (ScriptException e) {
            logger.warn(e, "执行脚本服务[{}]时发生异常！", name);

            return null;
        }
    }

    @Override
    public List<String> names() {
        List<String> names = new ArrayList<>();
        try {
            String root = context.getAbsolutePath(path);
            BufferedReader reader = new BufferedReader(new FileReader(root + "/name"));
            for (String string; (string = reader.readLine()) != null; )
                if (!validator.isEmpty(string))
                    names.add(string);
            reader.close();
        } catch (IOException e) {
            logger.warn(e, "读取脚本文件名时发生异常！");
        }

        return names;
    }

    @Override
    public String read(String name) {
        return new String(io.read(context.getAbsolutePath(path) + name));
    }

    protected ScriptEngine get() {
        if (engine == null)
            refresh();

        return engine;
    }
}
