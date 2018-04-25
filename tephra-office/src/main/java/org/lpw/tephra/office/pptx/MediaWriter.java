package org.lpw.tephra.office.pptx;

import java.io.InputStream;

/**
 * 媒体资源输出器。
 *
 * @author lpw
 */
public interface MediaWriter {
    /**
     * 资源类型。
     */
    enum Type {
        /**
         * 图片。
         */
        Image
    }

    /**
     * 输出媒体资源。
     *
     * @param type        资源类型。
     * @param contentType 内容类型。
     * @param inputStream 资源流。
     * @return 资源获取路径。
     */
    String write(Type type, String contentType, InputStream inputStream);
}
