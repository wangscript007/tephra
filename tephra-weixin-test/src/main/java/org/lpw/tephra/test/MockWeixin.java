package org.lpw.tephra.test;

import java.util.List;

/**
 * @author lpw
 */
public interface MockWeixin {
    /**
     * 重置微信Mock环境。
     */
    void reset();

    /**
     * Mock微信认证。
     *
     * @param openId   OpenID。
     * @param nickname 昵称。
     * @param portrait 头像。
     */
    void auth(String openId, String nickname, String portrait);

    /**
     * 获取请求参数集。
     *
     * @return 请求参数集。
     */
    List<Object[]> getArgs();
}
