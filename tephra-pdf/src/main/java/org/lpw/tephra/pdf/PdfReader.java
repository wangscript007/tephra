package org.lpw.tephra.pdf;

import com.alibaba.fastjson.JSONObject;

import java.io.InputStream;
import java.util.List;

/**
 * PDF读取器。
 *
 * @author lpw
 */
public interface PdfReader {
    /**
     * 读取并解析PDF数据。
     *
     * @param inputStream 输入流。
     * @param mediaWriter 媒体输出器。
     * @return JSON数据。
     */
    JSONObject read(InputStream inputStream, MediaWriter mediaWriter);

    /**
     * 转化为PNG图片。
     *
     * @param inputStream 输入流。
     * @param mediaWriter 媒体输出器。
     * @param page        转化页码，0表示第一页。
     * @return 图片URL地址。
     */
    String png(InputStream inputStream, MediaWriter mediaWriter, int page);

    /**
     * 转化为PNG图片集。
     *
     * @param inputStream 输入流。
     * @param mediaWriter 媒体输出器。
     * @return 图片URL地址集，首张为各页合并图。
     */
    List<String> pngs(InputStream inputStream, MediaWriter mediaWriter);
}
