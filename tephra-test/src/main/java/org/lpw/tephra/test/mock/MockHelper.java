package org.lpw.tephra.test.mock;

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
     * 获取Mock输出实例。
     *
     * @return Mock输出实例。
     */
    MockResponse getResponse();

    /**
     * 重置Mock环境。
     */
    void reset();

    /**
     * 以Mock方式执行请求。
     *
     * @param uri 请求URI地址。
     */
    void mock(String uri);

    /**
     * 以Mock方式执行请求。
     *
     * @param web WebApp目录，支持相对路径与绝对路径。
     * @param uri 请求URI地址。
     */
    void mock(String web, String uri);
}
