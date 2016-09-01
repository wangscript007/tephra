package org.lpw.tephra.dao.model;

import java.lang.annotation.*;

/**
 * 内存表定义。
 *
 * @author lpw
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Memory {
    /**
     * 内存表名称。
     *
     * @return 内存表名称。
     */
    String name();
}
