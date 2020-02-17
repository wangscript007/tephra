package org.lpw.tephra.ctrl.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 跨域。
 */
public interface Cors {
    /**
     * 是否跨域请求。
     *
     * @param request  请求。
     * @param response 输出。
     * @return 如果是则返回true；否则返回false。
     */
    boolean is(HttpServletRequest request, HttpServletResponse response);

    /**
     * 设置跨域。
     *
     * @param request  请求。
     * @param response 输出。
     */
    void set(HttpServletRequest request, HttpServletResponse response);
}
