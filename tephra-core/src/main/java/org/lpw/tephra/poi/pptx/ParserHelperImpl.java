package org.lpw.tephra.poi.pptx;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xslf.usermodel.XSLFSimpleShape;
import org.lpw.tephra.bean.BeanFactory;
import org.lpw.tephra.bean.ContextRefreshedListener;
import org.lpw.tephra.util.Json;
import org.lpw.tephra.util.Numeric;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lpw
 */
@Component("tephra.poi.pptx.parser-helper")
public class ParserHelperImpl implements ParserHelper, ContextRefreshedListener {
    @Inject
    private Numeric numeric;
    @Inject
    private Json json;
    private Map<String, Parser> parsers;

    @Override
    public Parser get(String type) {
        return parsers.get(type);
    }

    @Override
    public Rectangle getRectangle(JSONObject object) {
        return new Rectangle(object.getIntValue("x"), object.getIntValue("y"),
                object.getIntValue("width"), object.getIntValue("height"));
    }

    @Override
    public void rotate(XSLFSimpleShape xslfSimpleShape, JSONObject object) {
        if (object.containsKey("rotation"))
            xslfSimpleShape.setRotation(object.getDoubleValue("rotation"));
        if (json.hasTrue(object, "rotationX"))
            xslfSimpleShape.setFlipVertical(true);
        if (json.hasTrue(object, "rotationY"))
            xslfSimpleShape.setFlipHorizontal(true);
    }

    @Override
    public Color getColor(JSONObject object, String key) {
        if (!object.containsKey(key))
            return null;

        String color = object.getString(key);
        int[] ns;
        if (color.charAt(0) == '#') {
            String[] array = new String[3];
            boolean full = color.length() == 7;
            for (int i = 0; i < array.length; i++)
                array[i] = full ? color.substring(2 * i + 1, 2 * i + 3) : (color.substring(i + 1, i + 2) + color.substring(i + 1, i + 2));
            ns = new int[3];
            for (int i = 0; i < ns.length; i++)
                ns[i] = Integer.parseInt(array[i], 16);
        } else if (color.indexOf('(') > -1)
            ns = numeric.toInts(color.substring(color.indexOf('(') + 1, color.indexOf(')')));
        else
            ns = numeric.toInts(color);

        return new Color(ns[0], ns[1], ns[2]);
    }

    @Override
    public byte[] getImage(JSONObject object, String contentType, ByteArrayOutputStream outputStream) throws IOException {
        byte[] bytes = outputStream.toByteArray();
        if (json.hasTrue(object, "thumbnail"))
            return thumbnail(bytes, object, getFormat(contentType));

        if (object.containsKey("subimage"))
            return subimage(bytes, object.getJSONObject("subimage"), getFormat(contentType));

        return bytes;
    }

    private String getFormat(String contentType) {
        switch (contentType) {
            case "image/jpeg":
                return "JPEG";
            case "image/gif":
                return "GIT";
            default:
                return "PNG";
        }
    }

    private byte[] subimage(byte[] data, JSONObject object, String format) throws IOException {
        BufferedImage image = toImage(data);
        if (image == null)
            return data;

        int x = object.getIntValue("x");
        int y = object.getIntValue("y");
        int w = object.getIntValue("width");
        int h = object.getIntValue("height");
        int width = image.getWidth();
        int height = image.getHeight();
        if (x == 0 && y == 0 && w >= width && h >= height)
            return data;

        return toBytes(image.getSubimage(x, y, Math.min(w, width - x), Math.min(h, height - y)), format);
    }

    private byte[] thumbnail(byte[] data, JSONObject object, String format) throws IOException {
        BufferedImage image = toImage(data);
        if (image == null)
            return data;

        int width = object.getIntValue("width");
        int height = object.getIntValue("height");
        BufferedImage thumbnail = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        thumbnail.getGraphics().drawImage(image, 0, 0, width, height, null);

        return toBytes(thumbnail, format);
    }

    private BufferedImage toImage(byte[] data) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        BufferedImage image = ImageIO.read(inputStream);
        inputStream.close();

        return image;
    }

    private byte[] toBytes(BufferedImage image, String format) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, format, outputStream);
        outputStream.close();

        return outputStream.toByteArray();
    }

    @Override
    public int getContextRefreshedSort() {
        return 8;
    }

    @Override
    public void onContextRefreshed() {
        parsers = new HashMap<>();
        BeanFactory.getBeans(Parser.class).forEach(parser -> parsers.put(parser.getType(), parser));
    }
}
