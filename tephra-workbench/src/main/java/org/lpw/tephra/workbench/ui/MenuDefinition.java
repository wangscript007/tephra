package org.lpw.tephra.workbench.ui;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 菜单定义。
 *
 * @author lpw
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MenuDefinition {
    /**
     * 主菜单显示顺序，0表示由其他模块设置。
     *
     * @return 主菜单显示顺序。
     */
    int parent() default 0;

    /**
     * 显示顺序。
     *
     * @return 显示顺序。
     */
    int sort();
}
