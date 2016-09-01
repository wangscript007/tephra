package org.lpw.tephra.ctrl;

import org.lpw.tephra.ctrl.context.Response;

/**
 * 调度。用于对请求进行分发。
 *
 * @author lpw
 */
public interface Dispatcher {
    /**
     * 执行服务。
     *
     * @param response 响应。
     */
    void execute(Response response);
}
