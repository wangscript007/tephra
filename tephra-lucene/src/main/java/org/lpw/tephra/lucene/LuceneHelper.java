package org.lpw.tephra.lucene;

import java.util.List;

/**
 * Lucene全文检索。
 *
 * @author lpw
 */
public interface LuceneHelper {
    /**
     * 清空数据。
     *
     * @param key 引用key。
     */
    void clear(String key);

    /**
     * 更新数据。
     *
     * @param key  引用key。
     * @param id   数据ID。
     * @param data 数据。
     */
    void source(String key, String id, String data);

    /**
     * 创建索引。
     *
     * @param key 引用key。
     * @return 更新数量；失败则返回-1。
     */
    int index(String key);

    /**
     * 检索。
     *
     * @param key   引用key。
     * @param words 关键词集。
     * @param and   是否AND关键词。
     * @param size  最大返回数。
     * @return 数据ID值集；如果关键词为空则返回null。
     */
    List<String> query(String key, String[] words, boolean and, int size);

    /**
     * 检索。
     *
     * @param key   引用key。
     * @param words 关键词集。
     * @param and   是否AND关键词。
     * @param size  最大返回数。
     * @return 数据ID值集；如果关键词为空则返回null。
     */
    List<String> query(String key, List<String> words, boolean and, int size);

    /**
     * 检索。
     *
     * @param key    引用key。
     * @param string 检索规则。
     * @param and    是否AND关键词。
     * @param size   最大返回数。
     * @return 数据ID值集；如果关键词为空则返回null。
     */
    List<String> query(String key, String string, boolean and, int size);
}
