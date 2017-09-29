package org.lpw.tephra.poi.pptx;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFPictureData;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.lpw.tephra.util.Http;
import org.lpw.tephra.util.Logger;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.util.Map;

/**
 * @author lpw
 */
@Component("tephra.poi.pptx.image")
public class ImageParserImpl extends ParserSupport implements Parser {
    @Inject
    private Http http;
    @Inject
    private Logger logger;

    @Override
    public String getType() {
        return "image";
    }

    @Override
    public void parse(XMLSlideShow xmlSlideShow, XSLFSlide xslfSlide, JSONObject object) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Map<String, String> map = http.download(object.getString("image"), null, null, outputStream);
        if (map == null)
            return;

        XSLFPictureData xslfPictureData = xmlSlideShow.addPicture(outputStream.toByteArray(), getPictureType(map.get("Content-Type")));
        XSLFPictureShape xslfPictureShape = xslfSlide.createPicture(xslfPictureData);
        xslfPictureShape.setAnchor(getRectangle(object));
        rotate(xslfPictureShape, object);
    }

    private PictureData.PictureType getPictureType(String contentType) {
        if (contentType.equals("image/jpeg"))
            return PictureData.PictureType.JPEG;

        if (!contentType.equals("image/png"))
            logger.warn(null, "未处理图片类型[{}]！", contentType);

        return PictureData.PictureType.PNG;
    }
}
