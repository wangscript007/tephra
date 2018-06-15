package org.lpw.tephra.lucene;

import java.util.Set;

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
     * @param size  最大返回数。
     * @return 数据ID值集。
     */
    Set<String> query(String key, Set<String> words, int size);
}
