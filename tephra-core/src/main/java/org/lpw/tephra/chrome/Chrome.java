package org.lpw.tephra.chrome;

/**
 * Google Chrome DevTools协议工具。
 * https://div.io/topic/1464
 * https://chromedevtools.github.io/devtools-protocol/tot/Page/
 * https://github.com/spmallette/netty-example/blob/master/src/test/java/com/genoprime/netty/example/WebSocketClient.java
 *
 * @author lpw
 */
public interface Chrome {
    /**
     * 输出PDF文档。
     *
     * @param url    URL地址。
     * @param wait   等待时间，单位：秒。
     * @param width  页面宽度，单位：像素。
     * @param height 页面高度，单位：像素。
     * @return Base64编码的PDF数据。
     */
    byte[] pdf(String url, int wait, int width, int height);
}
