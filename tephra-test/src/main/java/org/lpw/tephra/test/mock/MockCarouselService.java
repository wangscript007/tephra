package org.lpw.tephra.test.mock;

import java.util.Map;

/**
 * @author lpw
 */
public interface MockCarouselService {
    /**
     * 执行服务。
     *
     * @param key       服务key。
     * @param header    头信息。
     * @param parameter 请求参数。
     * @param cacheable 是否允许缓存和使用缓存数据。
     * @return 执行结果。
     */
    String service(String key, Map<String, String> header, Map<String, String> parameter, boolean cacheable);
}
