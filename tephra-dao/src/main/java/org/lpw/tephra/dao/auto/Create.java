package org.lpw.tephra.dao.auto;

import java.util.Map;
import java.util.Set;

/**
 * 执行创建DDL。
 *
 * @author lpw
 */
interface Create {
    /**
     * 执行创建DDL。
     *
     * @param tables 表名称集。
     */
    void execute(Map<String, Set<String>> tables);
}
