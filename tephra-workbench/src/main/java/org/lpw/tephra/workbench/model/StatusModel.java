package org.lpw.tephra.workbench.model;

import org.lpw.tephra.dao.model.Model;

/**
 * @author lpw
 */
public interface StatusModel extends Model {
    /**
     * 获取状态值。
     *
     * @return 状态值。
     */
    int getStatus();

    /**
     * 设置状态值。
     *
     * @param status 状态值。
     */
    void setStatus(int status);
}
