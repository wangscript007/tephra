package org.lpw.tephra.office.pptx.parser;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xslf.usermodel.XSLFPictureData;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;
import org.apache.poi.xslf.usermodel.XSLFSimpleShape;
import org.lpw.tephra.office.pptx.MediaWriter;
import org.lpw.tephra.util.Logger;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.awt.Dimension;
import java.io.IOException;

/**
 * @author lpw
 */
@Component("tephra.office.pptx.parser.image")
public class ImageParserImpl implements Parser {
    @Inject
    private Logger logger;

    @Override
    public int getSort() {
        return 5;
    }

    @Override
    public void parse(XSLFSimpleShape xslfSimpleShape, MediaWriter mediaWriter, JSONObject shape) {
        if (!(xslfSimpleShape instanceof XSLFPictureShape))
            return;

        XSLFPictureShape xslfPictureShape = (XSLFPictureShape) xslfSimpleShape;
        JSONObject image = new JSONObject();
        XSLFPictureData xslfPictureData = xslfPictureShape.getPictureData();
        parseSize(xslfPictureData, image);
        image.put("contentType", xslfPictureData.getContentType());
        try {
            image.put("url", mediaWriter.write(MediaWriter.Type.Image, xslfPictureData.getContentType(), xslfPictureData.getInputStream()));
        } catch (IOException e) {
            logger.warn(e, "获取PPTX图片数据时发生异常！");
        }
        shape.put("image", image);
    }

    private void parseSize(XSLFPictureData xslfPictureData, JSONObject image) {
        Dimension dimension = xslfPictureData.getImageDimensionInPixels();
        JSONObject size = new JSONObject();
        size.put("width", dimension.width);
        size.put("height", dimension.height);
        image.put("size", size);
    }
}
