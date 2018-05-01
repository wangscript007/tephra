package org.lpw.tephra.office.pptx;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFGroupShape;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSimpleShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.lpw.tephra.office.pptx.parser.Parsers;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Numeric;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.awt.Dimension;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

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
                parseLayout(xslfSlide, mediaWriter, null);

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

    private void parseLayout(XSLFSlide xslfSlide, MediaWriter mediaWriter, Map<String, Map<Integer, JSONObject>> map) {
        System.out.println("############################################################");
        parseShapes(xslfSlide.getSlideLayout().getShapes(), mediaWriter, new JSONArray());
        System.out.println("#######");
//        XSLFSlideLayout xslfSlideLayout = xslfSlide.getSlideLayout();
//        if (map.containsKey(xslfSlideLayout.getName()))
//            return;
    }

    private void parseSlide(XSLFSlide xslfSlide, MediaWriter mediaWriter, JSONObject slide) {
        parseBackground(xslfSlide, mediaWriter, slide);

        JSONArray shapes = new JSONArray();
        parseShapes(xslfSlide.getShapes(), mediaWriter, shapes);
        slide.put("shapes", shapes);
    }

    private void parseBackground(XSLFSlide xslfSlide, MediaWriter mediaWriter, JSONObject slide) {
        JSONObject background = new JSONObject();
        parsers.parse(xslfSlide.getBackground(), mediaWriter, background);
        background.remove("anchor");
        if (!background.isEmpty())
            slide.put("background", background);
    }

    private void parseShapes(List<XSLFShape> xslfSlides, MediaWriter mediaWriter, JSONArray shapes) {
        xslfSlides.forEach(xslfShape -> {
            if (xslfShape instanceof XSLFSimpleShape) {
                JSONObject shape = new JSONObject();
                parsers.parse((XSLFSimpleShape) xslfShape, mediaWriter, shape);
                shapes.add(shape);

                return;
            }

            if (xslfShape instanceof XSLFGroupShape) {
                parseShapes(((XSLFGroupShape) xslfShape).getShapes(), mediaWriter, shapes);

                return;
            }

            logger.warn(null, "无法处理的PPTX图形[{}]。", xslfShape);
        });
    }
}
