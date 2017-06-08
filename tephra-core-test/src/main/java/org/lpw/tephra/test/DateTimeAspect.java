package org.lpw.tephra.test;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author lpw
 */
public interface DateTimeAspect {
    /**
     * 设置当前时间集。
     *
     * @param nows 当前时间集。
     */
    void now(List<Timestamp> nows);
}
