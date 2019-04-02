package org.lpw.tephra.office;

/**
 * 资源类型。
 *
 * @author lpw
 */
public enum MediaType {
    /**
     * JPEG图片。
     */
    Jpeg("image/jpeg", ".jpeg"),
    /**
     * PNG图片。
     */
    Png("image/png", ".png"),
    /**
     * GIF图片。
     */
    Gif("image/gif", ".gif"),
    /**
     * SVG图片。
     */
    Svg("image/svg+xml", ".svg");

    private String contentType;
    private String suffix;

    MediaType(String contentType, String suffix) {
        this.contentType = contentType;
        this.suffix = suffix;
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
