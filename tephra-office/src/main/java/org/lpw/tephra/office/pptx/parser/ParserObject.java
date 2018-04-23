package org.lpw.tephra.office.pptx.parser;

import com.alibaba.fastjson.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lpw
 */
public class ParserObject {
    private int width;
    private int height;
    private Map<String, int[]> imageSizes = new HashMap<>();
    private Map<String, String> imageRels = new HashMap<>();
    private List<JSONArray> slides = new ArrayList<>();

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void addImage(String uri, int[] size) {
        imageSizes.put(uri, size);
    }

    public int[] getImageSize(String key) {
        return imageSizes.get(imageRels.getOrDefault(key, key));
    }

    public void addImageRel(String key, String uri) {
        imageRels.put(key, uri);
    }

    public String getImageUri(String key) {
        return imageRels.get(key);
    }

    public void addSlide(JSONArray elements) {
        slides.add(elements);
    }

    public List<JSONArray> getSlides() {
        return slides;
    }
}
