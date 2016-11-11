package org.lpw.tephra.storage;

/**
 * 文件存储处理器集。
 *
 * @auth lpw
 */
public interface Storages {
    /**
     * 获取默认文件处理器。
     *
     * @return 文件处理器；如果不存在则返回null。
     */
    Storage get();

    /**
     * 获取文件处理器。
     *
     * @param type 类型；为空则使用默认。
     * @return 文件处理器；如果不存在则返回null。
     */
    Storage get(String type);
}
