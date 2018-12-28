package org.lpw.tephra.dao.auto;

import java.util.Map;
import java.util.Set;

/**
 * 创建每日表。
 *
 * @author lpw
 */
public interface Daily {
    /**
     * 创建每日表。
     *
     * @param tables 表名称集。
     */
    void execute(Map<String, Set<String>> tables);
}
