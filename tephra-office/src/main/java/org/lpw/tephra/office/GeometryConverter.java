package org.lpw.tephra.office;

import java.io.File;

/**
 * 几何图形转换器。
 *
 * @author lpw
 */
public interface GeometryConverter {
    /**
     * 获取几何图形图。
     *
     * @param type 图形类型。
     * @return 图文件URI，不存在则返回null。
     */
    String getImage(String type);

    /**
     * 保存几何图形。
     *
     * @param type 图形类型。
     * @param file 图形文件。
     * @return 图文件URI，不存在则返回null。
     */
    String saveImage(String type, File file);
}
