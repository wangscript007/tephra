package org.lpw.tephra.poi.pptx;

import com.alibaba.fastjson.JSONObject;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFPictureData;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.lpw.tephra.poi.StreamWriter;
import org.lpw.tephra.util.Converter;
import org.lpw.tephra.util.Http;
import org.lpw.tephra.util.Logger;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author lpw
 */
@Component("tephra.poi.pptx.svg")
public class SvgParserImpl extends ImageParserSupport implements Parser {
    @Inject
    private Http http;
    @Inject
    private Converter converter;
    @Inject
    private Logger logger;
    private Pattern pattern = Pattern.compile("viewBox=\"[^\"]+\"");

    @Override
    public String getType() {
        return TYPE_SVG;
    }

    @Override
    public boolean parse(XMLSlideShow xmlSlideShow, XSLFSlide xslfSlide, JSONObject object) {
        try {
            XSLFPictureData xslfPictureData = xmlSlideShow.addPicture(parserHelper.getImage(object, "image/png",
                    readSvg(object, object.getString("svg"))), PictureData.PictureType.PNG);
            parse(xslfSlide, xslfPictureData, object);

            return true;
        } catch (Throwable e) {
            logger.warn(e, "解析SVG图片[{}]时发生异常！", object.toJSONString());

            return false;
        }
    }

    private ByteArrayOutputStream readSvg(JSONObject object, String image) throws IOException, TranscoderException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Reader reader = new StringReader(image);
        PNGTranscoder pngTranscoder = new PNGTranscoder();
        float[] wh = getWidthHeight(object, image);
        pngTranscoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, wh[0]);
        pngTranscoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, wh[1]);
        pngTranscoder.transcode(new TranscoderInput(reader), new TranscoderOutput(outputStream));
        reader.close();
        outputStream.flush();
        outputStream.close();

        return outputStream;
    }

    private float[] getWidthHeight(JSONObject object, String image) {
        float[] wh = new float[]{numeric.toFloat(object.getIntValue("width")), numeric.toFloat(object.getIntValue("height"))};

        Matcher matcher = pattern.matcher(image);
        if (!matcher.find())
            return wh;

        String viewBox = matcher.group();
        String[] array = converter.toArray(viewBox.substring(9, viewBox.length() - 1), " ");
        float width = numeric.toFloat(array[2]);
        float height = numeric.toFloat(array[3]);
        if (width > wh[0] || height > wh[1]) {
            wh[0] = width;
            wh[1] = height;
        }

        return wh;
    }

    @Override
    public boolean parse(JSONObject object, XSLFShape xslfShape, StreamWriter writer) {
        return false;
    }
}
