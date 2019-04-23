package org.lpw.tephra.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 压缩/解压缩工具。
 *
 * @author lpw
 */
public interface Zipper {
    /**
     * 打包压缩文件集。
     *
     * @param input  待压缩文件集。
     * @param output 输出文件。
     * @throws IOException IO异常。
     */
    void zip(List<File> input, File output) throws IOException;

    /**
     * 打包压缩文件集。
     *
     * @param input  待压缩文件集，key为输出文件名，value为文件。
     * @param output 输出文件。
     * @throws IOException IO异常。
     */
    void zip(Map<String, File> input, File output) throws IOException;

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
