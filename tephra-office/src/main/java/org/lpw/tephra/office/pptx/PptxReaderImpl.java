package org.lpw.tephra.office.pptx;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSimpleShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.lpw.tephra.office.pptx.parser.Parsers;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Numeric;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.awt.Dimension;
import java.io.InputStream;

/**
 * @author lpw
 */
@Component("tephra.office.pptx.reader")
public class PptxReaderImpl implements PptxReader {
    @Inject
    private Numeric numeric;
    @Inject
    private Logger logger;
    @Inject
    private Parsers parsers;

    @Override
    public JSONObject read(InputStream inputStream, MediaWriter mediaWriter) {
        JSONObject object = new JSONObject();
        try {
            XMLSlideShow xmlSlideShow = new XMLSlideShow(inputStream);
            parseSize(xmlSlideShow, object);
            JSONArray slides = new JSONArray();
            xmlSlideShow.getSlides().forEach(xslfSlide -> {
                JSONObject slide = new JSONObject();
                parseSlide(xslfSlide, mediaWriter, slide);
                slides.add(slide);
            });
            object.put("slides", slides);
            xmlSlideShow.close();
        } catch (Exception e) {
            logger.warn(e, "读取PPTX数据时发生异常！");
        }

        return object;
    }

    private void parseSize(XMLSlideShow xmlSlideShow, JSONObject object) {
        JSONObject size = new JSONObject();
        Dimension dimension = xmlSlideShow.getPageSize();
        size.put("width", dimension.width);
        size.put("height", dimension.height);
        object.put("size", size);
    }

    private void parseSlide(XSLFSlide xslfSlide, MediaWriter mediaWriter, JSONObject slide) {
        parsers.parse(xslfSlide.getBackground(), mediaWriter, slide);
        JSONArray shapes = new JSONArray();
        xslfSlide.getShapes().forEach(xslfShape -> {
            JSONObject shape = new JSONObject();
            parsers.parse((XSLFSimpleShape) xslfShape, mediaWriter, shape);
            shapes.add(shape);
        });
        slide.put("shapes", shapes);
    }
}
