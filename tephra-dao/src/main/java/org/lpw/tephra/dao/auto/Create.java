package org.lpw.tephra.dao.auto;

import org.lpw.tephra.dao.model.Model;

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

    /**
     * 读取create.sql数据。
     *
     * @param modelClass Model类。
     * @return create.sql数据，不存在或读取失败则返回null。
     */
    String[] read(Class<? extends Model> modelClass);
}
