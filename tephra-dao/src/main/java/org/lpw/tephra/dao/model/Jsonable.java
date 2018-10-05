package org.lpw.tephra.dao.model;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 定义可转化为JSON的属性。
 *
 * @author lpw
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Jsonable {
    /**
     * 数据格式。
     *
     * @return 数据格式。
     */
    String format() default "";

    /**
     * 是否返回时间戳。
     * 如果true则返回JSON数据时增加返回Timestamp对象的时间戳，key为：name+Timestamp。
     *
     * @return 是否返回时间戳。
     */
    boolean timestamp() default false;

    /**
     * 是否为扩展属性。
     *
     * @return 是否为扩展属性。
     */
    boolean extend() default false;
}
