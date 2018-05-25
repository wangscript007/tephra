package org.lpw.tephra.util;

import java.util.Locale;

/**
 * 运行上下文。
 *
 * @author lpw
 */
public interface Context {
    /**
     * 获取根绝对路径值。
     *
     * @return 根绝对路径值。
     */
    String getAbsoluteRoot();

    /**
     * 获取绝对路径。
     *
     * @param path 相对路径。
     * @return 绝对路径。
     */
    String getAbsolutePath(String path);

    /**
     * 获取字符集编码。
     *
     * @param charset 字符集编码，如果为空则使用默认字符集编码。
     * @return 字符集编码。
     */
    String getCharset(String charset);

    /**
     * 设置本地化信息。仅当前线程有效。
     *
     * @param locale 本地化信息。
     */
    void setLocale(Locale locale);

    /**
     * 获取本地化信息。
     *
     * @return 本地化信息；如果未设置则使用默认值。
     */
    Locale getLocale();

    /**
     * 清空当前线程上下文。
     */
    void clearThreadLocal();

    /**
     * 保存数据到当前线程上下文中。
     *
     * @param key   引用key。
     * @param value 数据。
     */
    void putThreadLocal(String key, Object value);

    /**
     * 获取当前线程上下文数据。
     *
     * @param key 引用key。
     * @param <T> 数据类型。
     * @return 当前线程上下文数据。
     */
    <T> T getThreadLocal(String key);

    /**
     * 删除当前线程上下文数据。
     *
     * @param key 引用key。
     * @param <T> 数据类型。
     * @return 当前线程上下文数据。
     */
    <T> T removeThreadLocal(String key);
}
