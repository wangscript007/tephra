package org.lpw.tephra.chrome;

/**
 * Google Chrome DevTools协议工具。
 * https://chromedevtools.github.io/devtools-protocol/
 * https://github.com/ChromeDevTools/awesome-chrome-devtools
 *
 * @author lpw
 */
public interface ChromeHelper {
    /**
     * 输出PDF文档。
     *
     * @param url    URL地址。
     * @param wait   等待时间，单位：秒。
     * @param width  页面宽度，单位：像素。
     * @param height 页面高度，单位：像素。
     * @param range  输出的页面。
     * @return PDF文件路径。
     */
    String pdf(String url, int wait, int width, int height, String range);

    /**
     * 输出PNG图片。
     *
     * @param url    URL地址。
     * @param wait   等待时间，单位：秒。
     * @param x      X位置，单位：像素。
     * @param y      Y位置，单位：像素。
     * @param width  页面宽度，单位：像素。
     * @param height 页面高度，单位：像素。
     * @return PNG文件路径。
     */
    String png(String url, int wait, int x, int y, int width, int height);

    /**
     * 输出JPEG图片。
     *
     * @param url    URL地址。
     * @param wait   等待时间，单位：秒。
     * @param x      X位置，单位：像素。
     * @param y      Y位置，单位：像素。
     * @param width  页面宽度，单位：像素。
     * @param height 页面高度，单位：像素。
     * @return JPEG文件路径。
     */
    String jpeg(String url, int wait, int x, int y, int width, int height);
}
