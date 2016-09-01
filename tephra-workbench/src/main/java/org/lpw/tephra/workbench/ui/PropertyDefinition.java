package org.lpw.tephra.workbench.ui;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 属性定义。
 *
 * @author lpw
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PropertyDefinition {
    /**
     * 显示顺序。
     *
     * @return 显示顺序。
     */
    int sort();

    /**
     * 显示标签资源key；如果未设置则使用默认规则生成。
     *
     * @return 显示标签资源key。
     */
    String labelKey() default "";

    /**
     * 是否可编辑；默认为true。
     *
     * @return 是否可编辑。
     */
    boolean editable() default true;

    /**
     * 类型；默认为文本框。
     *
     * @return 类型。
     */
    PropertyType type() default PropertyType.Text;

    /**
     * 搜索类型；默认为不可搜索。
     *
     * @return 搜索类型。
     */
    SearchType search() default SearchType.Null;
}
