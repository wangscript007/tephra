package org.lpw.tephra.pdf;

/**
 * 资源类型。
 *
 * @author lpw
 */
public enum MediaType {
    /**
     * JPEG图片。
     */
    Jpeg("JPEG", "image/jpeg", ".jpeg"),
    /**
     * PNG图片。
     */
    Png("PNG", "image/png", ".png"),
    /**
     * GIF图片。
     */
    Gif("GIF", "image/gif", ".gif"),
    /**
     * SVG图片。
     */
    Svg("SVG", "image/svg+xml", ".svg");

    private String formatName;
    private String contentType;
    private String suffix;

    MediaType(String formatName, String contentType, String suffix) {
        this.formatName = formatName;
        this.contentType = contentType;
        this.suffix = suffix;
    }

    /**
     * 获取媒体格式名称。
     *
     * @return 媒体格式名称。
     */
    public String getFormatName() {
        return formatName;
    }

    /**
     * 获取媒体Content Type。
     *
     * @return 媒体Content Type。
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * 获取媒体后缀。
     *
     * @return 媒体后缀。
     */
    public String getSuffix() {
        return suffix;
    }

    /**
     * 查找媒体类型。
     *
     * @param contentType Content Type。
     * @return 媒体类型；不存在则返回null。
     */
    public static MediaType find(String contentType) {
        for (MediaType mediaType : MediaType.values())
            if (mediaType.contentType.equals(contentType))
                return mediaType;

        return null;
    }
}
