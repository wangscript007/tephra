package org.lpw.tephra.ctrl.mock;

/**
 * Mock环境支持接口。
 *
 * @author lpw
 */
public interface MockHelper {
    /**
     * 获取Mock请求头实例。
     *
     * @return Mock请求头实例。
     */
    MockHeader getHeader();

    /**
     * 获取Mock请求Session环境。
     *
     * @return Mock请求Session环境。
     */
    MockSession getSession();

    /**
     * 获取Mock请求实例。
     *
     * @return Mock请求实例。
     */
    MockRequest getRequest();

    /**
     * 以Mock方式执行请求。
     *
     * @param uri 请求URI地址。
     * @return 输出。
     */
    MockResponse mock(String uri);

    /**
     * 以Mock方式执行请求。
     *
     * @param web WebApp目录，支持相对路径与绝对路径。
     * @param uri 请求URI地址。
     * @return 输出。
     */
    MockResponse mock(String web, String uri);
}
