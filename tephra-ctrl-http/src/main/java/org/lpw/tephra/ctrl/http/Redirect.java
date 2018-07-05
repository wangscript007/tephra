package org.lpw.tephra.ctrl.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author lpw
 */
public interface Redirect {
    /**
     * 转发。
     *
     * @param request  请求。
     * @param uri      URI地址。
     * @param response 输出。
     * @return 如果可转发则返回true；否则返回false。
     * @throws IOException IO异常。
     */
    boolean redirect(HttpServletRequest request, String uri, HttpServletResponse response) throws IOException;
}
