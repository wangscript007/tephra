package org.lpw.tephra.office.pptx.parser;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFPictureData;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSimpleShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.lpw.tephra.office.MediaReader;
import org.lpw.tephra.office.MediaType;
import org.lpw.tephra.office.MediaWriter;
import org.lpw.tephra.office.OfficeHelper;
import org.lpw.tephra.util.Logger;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.awt.Dimension;
import java.awt.Insets;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author lpw
 */
@Component("tephra.office.pptx.parser.image")
public class ImageImpl implements Simple {
    @Inject
    private Logger logger;
    @Inject
    private OfficeHelper officeHelper;

    @Override
    public int getSort() {
        return 8;
    }

    @Override
    public void parseShape(XSLFSimpleShape xslfSimpleShape, MediaWriter mediaWriter, JSONObject shape, boolean layout) {
        if (!(xslfSimpleShape instanceof XSLFPictureShape))
            return;

        XSLFPictureShape xslfPictureShape = (XSLFPictureShape) xslfSimpleShape;
        parseClipping(xslfPictureShape, shape);
        JSONObject image = new JSONObject();
        XSLFPictureData xslfPictureData = xslfPictureShape.getPictureData();
        parseSize(xslfPictureData, image);
        image.put("contentType", xslfPictureData.getContentType());
        try {
            image.put("url", mediaWriter.write(MediaType.find(xslfPictureData.getContentType()),
                    xslfPictureData.getFileName(), xslfPictureData.getInputStream()));
        } catch (IOException e) {
            logger.warn(e, "获取PPTX图片数据时发生异常！");
        }
        shape.put("image", image);
    }

    private void parseClipping(XSLFPictureShape xslfPictureShape, JSONObject shape) {
        Insets insets = xslfPictureShape.getClipping();
        if (insets == null)
            return;

        JSONObject clipping = new JSONObject();
        clipping.put("left", officeHelper.fromPercent(insets.left));
        clipping.put("top", officeHelper.fromPercent(insets.top));
        clipping.put("right", officeHelper.fromPercent(insets.right));
        clipping.put("bottom", officeHelper.fromPercent(insets.bottom));
        shape.put("clipping", clipping);
    }

    private void parseSize(XSLFPictureData xslfPictureData, JSONObject image) {
        Dimension dimension = xslfPictureData.getImageDimensionInPixels();
        JSONObject size = new JSONObject();
        size.put("width", dimension.width);
        size.put("height", dimension.height);
        image.put("size", size);
    }

    @Override
    public XSLFShape createShape(XMLSlideShow xmlSlideShow, XSLFSlide xslfSlide, MediaReader mediaReader, JSONObject shape) {
        if (!shape.containsKey("image"))
            return null;

        JSONObject image = shape.getJSONObject("image");
        try {
            InputStream inputStream = mediaReader.read(image);
            XSLFPictureData xslfPictureData = xmlSlideShow.addPicture(inputStream,
                    getPictureType(image.getString("url"), image.getString("contentType")));
            inputStream.close();

            return xslfSlide.createPicture(xslfPictureData);
        } catch (Throwable throwable) {
            logger.warn(throwable, "获取图片资源[{}]时发生异常！", image);

            return null;
        }
    }

    private PictureData.PictureType getPictureType(String url, String contentType) {
        switch (contentType) {
            case "image/jpeg":
                return PictureData.PictureType.JPEG;
            case "image/gif":
                return PictureData.PictureType.GIF;
            default:
                if (!contentType.equals("image/png"))
                    logger.warn(null, "未处理图片类型[{}:{}]！", url, contentType);
                return PictureData.PictureType.PNG;
        }
    }

    @Override
    public void parseToShape(XSLFSimpleShape xslfSimpleShape, MediaReader mediaReader, JSONObject shape) {
    }
}
