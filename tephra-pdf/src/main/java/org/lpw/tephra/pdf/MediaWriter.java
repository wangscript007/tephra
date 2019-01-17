package org.lpw.tephra.pdf;

import java.io.InputStream;

/**
 * 媒体资源输出器。
 *
 * @author lpw
 */
public interface MediaWriter {
    /**
     * 输出媒体资源。
     *
     * @param mediaType   资源类型。
     * @param fileName    文件名。
     * @param inputStream 资源流。
     * @return 资源获取路径。
     */
    String write(MediaType mediaType, String fileName, InputStream inputStream);
}
