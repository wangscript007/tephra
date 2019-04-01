package org.lpw.tephra.office.pptx;

import com.alibaba.fastjson.JSONObject;
import org.lpw.tephra.office.MediaWriter;

import java.io.InputStream;
import java.util.List;

/**
 * PPTx读取器。
 *
 * @author lpw
 */
public interface PptxReader {
    /**
     * 读取并解析PPTX数据。
     *
     * @param inputStream 输入流。
     * @param mediaWriter 媒体输出器。
     * @return JSON数据。
     */
    JSONObject read(InputStream inputStream, MediaWriter mediaWriter);

    /**
     * 读取并输出为PNG图集。
     *
     * @param inputStream 输入流。
     * @param mediaWriter 媒体输出器。
     * @param merge       是否合并，如果是则将所有图片合并为一张，并保存为第一个元素。
     * @return PNG图集，首张为各页合并图。
     */
    List<String> pngs(InputStream inputStream, MediaWriter mediaWriter, boolean merge);
}
