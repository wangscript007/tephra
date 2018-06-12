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
    Jpeg("image/jpeg"),
    /**
     * PNG图片。
     */
    Png("image/png"),
    /**
     * GIF图片。
     */
    Gif("image/gif"),
    /**
     * SVG图片。
     */
    SVG("image/svg+xml");

    private String contentType;

    MediaType(String contentType) {
        this.contentType = contentType;
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
