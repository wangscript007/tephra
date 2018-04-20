package org.lpw.tephra.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * 压缩/解压缩工具。
 *
 * @author lpw
 */
public interface Zipper {
    /**
     * 解压缩。
     *
     * @param input  压缩文件。
     * @param output 输出目录。
     * @throws IOException IO异常。
     */
    void unzip(File input, File output) throws IOException;

    /**
     * 解压缩。
     *
     * @param inputStream 压缩输入流。
     * @param output      输出目录。
     * @throws IOException IO异常。
     */
    void unzip(InputStream inputStream, File output) throws IOException;
}
