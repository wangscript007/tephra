package org.lpw.tephra.ctrl.status;

import net.sf.json.JSONObject;
import org.lpw.tephra.bean.ContextRefreshedListener;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author lpw
 */
@Service("tephra.ctrl.status")
public class StatusImpl implements Status, ContextRefreshedListener {
    @Autowired
    protected Validator validator;
    @Autowired
    protected Logger logger;
    @Value("${tephra.ctrl.status.uri:/tephra/ctrl/status}")
    protected String uri;
    protected boolean enable;

    @Override
    public boolean isStatus(String uri) {
        return enable && this.uri.equals(uri);
    }

    @Override
    public JSONObject execute(int counter) {
        JSONObject json = new JSONObject();
        json.put("concurrent", counter);
        json.put("timestamp", System.currentTimeMillis());

        return json;
    }

    @Override
    public int getContextRefreshedSort() {
        return 8;
    }

    @Override
    public void onContextRefreshed() {
        enable = !validator.isEmpty(uri);
        if (logger.isInfoEnable())
            logger.info("设置服务状态启动状态：{}", enable);
    }
}
