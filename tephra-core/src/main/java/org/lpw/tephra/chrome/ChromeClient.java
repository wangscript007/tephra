package org.lpw.tephra.chrome;

import com.alibaba.fastjson.JSONObject;

import java.util.concurrent.Callable;

/**
 * Chrome连接客户端。
 *
 * @author lpw
 */
public interface ChromeClient extends Callable<byte[]> {
    ChromeClient set(String service, String url, int wait, JSONObject message);
}
