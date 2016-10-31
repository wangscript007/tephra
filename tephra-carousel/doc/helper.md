# CarouselHelper
通过CarouselHelper可以轻松访问Carousel服务：
```java
package org.lpw.tephra.carousel;

import java.util.Map;

/**
 * @auth lpw
 */
public interface CarouselHelper {
    /**
     * 更新配置。
     *
     * @param name          配置名称。
     * @param description   配置描述。
     * @param actionBuilder Action构造器。
     * @return 配置结果；配置成功则返回true，否则返回false。
     */
    boolean config(String name, String description, ActionBuilder actionBuilder);

    /**
     * 更新配置。
     *
     * @param name          配置名称。
     * @param description   配置描述。
     * @param actionBuilder Action构造器。
     * @param delay         延迟时间，单位：秒。
     * @param interval      重复执行间隔，单位：秒。
     * @param times         重复执行次数。
     * @param wait          是否等待执行结果；true-等待，false-不等待。
     * @return 配置结果；配置成功则返回true，否则返回false。
     */
    boolean config(String name, String description, ActionBuilder actionBuilder, int delay, int interval, int times, boolean wait);

    /**
     * 执行流程。
     *
     * @param name   流程配置名称。
     * @param delay  延迟时间，单位：秒。
     * @param header 请求头信息。
     * @param data   数据。
     * @return 执行结果。
     */
    String execute(String name, int delay, Map<String, String> header, String data);
}
```