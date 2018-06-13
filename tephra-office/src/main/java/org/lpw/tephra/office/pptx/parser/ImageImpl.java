package org.lpw.tephra.office.pptx.parser;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xslf.usermodel.XSLFPictureData;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;
import org.apache.poi.xslf.usermodel.XSLFSimpleShape;
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
    public void parse(XSLFSimpleShape xslfSimpleShape, MediaWriter mediaWriter, JSONObject shape, boolean layout) {
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
    public void parse(XSLFSimpleShape xslfSimpleShape, MediaReader mediaReader, JSONObject shape) {

    }
}
