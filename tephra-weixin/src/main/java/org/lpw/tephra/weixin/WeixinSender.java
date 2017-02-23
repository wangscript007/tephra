package org.lpw.tephra.weixin;

/**
 * @author lpw
 */
public interface WeixinSender {
    /**
     * 发送文本信息。
     *
     * @param appId    微信公众号AppID。
     * @param receiver 接收者OpenID。
     * @param text     发送内容。
     * @return 如果发送成功则返回true；否则返回false。
     */
    boolean sendText(String appId, String receiver, String text);

    /**
     * 发送链接文本信息。
     *
     * @param appId    微信公众号AppID。
     * @param receiver 接收者OpenID。
     * @param uri      跳转URI地址。
     * @param text     发送内容。
     * @return 如果发送成功则返回true；否则返回false。
     */
    boolean sendLink(String appId, String receiver, String uri, String text);

    /**
     * 发送图片信息。
     *
     * @param appId    微信公众号AppID。
     * @param receiver 接收者OpenID。
     * @param uri      图片URI地址。
     * @return 如果发送成功则返回true；否则返回false。
     */
    boolean sendImage(String appId, String receiver, String uri);

    /**
     * 发送语音信息。
     *
     * @param appId    微信公众号AppID。
     * @param receiver 接收者OpenID。
     * @param uri      语音URI地址。
     * @return 如果发送成功则返回true；否则返回false。
     */
    boolean sendVoice(String appId, String receiver, String uri);

    /**
     * 发送视频信息。
     *
     * @param appId       微信公众号AppID。
     * @param receiver    接收者OpenID。
     * @param uri         视频URI地址。
     * @param thumbnail   视频缩略图。
     * @param title       视频标题。
     * @param description 视频说明。
     * @return 如果发送成功则返回true；否则返回false。
     */
    boolean sendVideo(String appId, String receiver, String uri, String thumbnail, String title, String description);

    /**
     * 发送新闻。
     *
     * @param appId    微信公众号AppID。
     * @param receiver 接收者OpenID。
     * @param builder  新闻创建器实例。
     * @return 如果发送成功则返回true；否则返回false。
     */
    boolean sendNews(String appId, String receiver, NewsBuilder builder);
}
