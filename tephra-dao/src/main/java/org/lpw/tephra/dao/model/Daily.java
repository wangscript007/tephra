package org.lpw.tephra.dao.model;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 每日表。
 *
 * @author lpw
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Daily {
    /**
     * 过期天数，过期后表会被自动删除。
     *
     * @return 过期天数。
     */
    int overdue() default 7;
}
