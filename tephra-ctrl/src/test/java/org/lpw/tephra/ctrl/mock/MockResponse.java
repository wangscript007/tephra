package org.lpw.tephra.ctrl.mock;

import org.lpw.tephra.ctrl.context.Response;

/**
 * @author lpw
 */
public interface MockResponse extends Response {
    /**
     * 获取返回内容类型。
     *
     * @return 返回内容类型。
     */
    String getContentType();

    /**
     * 获取跳转地址。
     *
     * @return 跳转地址。
     */
    String getRedirectTo();
}
