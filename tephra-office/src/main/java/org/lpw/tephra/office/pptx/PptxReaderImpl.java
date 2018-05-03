package org.lpw.tephra.office.pptx;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFBackground;
import org.apache.poi.xslf.usermodel.XSLFGroupShape;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSimpleShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.lpw.tephra.office.pptx.parser.Parsers;
import org.lpw.tephra.util.Json;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Numeric;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.awt.Dimension;
import java.io.InputStream;
import java.util.HashMap;
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
    private Json json;
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
            Map<String, Map<Integer, String>> layouts = new HashMap<>();
            JSONArray slides = new JSONArray();
            xmlSlideShow.getSlides().forEach(xslfSlide -> {
                JSONObject slide = new JSONObject();
                parseSlide(xslfSlide, mediaWriter, slide, parseLayout(xslfSlide, mediaWriter, layouts));
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

    private Map<Integer, String> parseLayout(XSLFSlide xslfSlide, MediaWriter mediaWriter, Map<String, Map<Integer, String>> layouts) {
        String name = xslfSlide.getSlideLayout().getName();
        if (layouts.containsKey(name))
            return layouts.get(name);

        Map<Integer, String> layout = new HashMap<>();
        parseBackground(xslfSlide.getSlideLayout().getBackground(), mediaWriter, null, layout);
        parseShapes(xslfSlide.getSlideLayout().getShapes(), mediaWriter, null, layout);
        layouts.put(name, layout);

        return layout;
    }

    private void parseSlide(XSLFSlide xslfSlide, MediaWriter mediaWriter, JSONObject slide, Map<Integer, String> layout) {
        parseBackground(xslfSlide.getBackground(), mediaWriter, slide, layout);

        JSONArray shapes = new JSONArray();
        parseShapes(xslfSlide.getShapes(), mediaWriter, shapes, layout);
        slide.put("shapes", shapes);
    }

    private void parseBackground(XSLFBackground xslfBackground, MediaWriter mediaWriter, JSONObject slide, Map<Integer, String> layout) {
        JSONObject background = copyOrNew(layout, 0);
        parsers.parse(xslfBackground, mediaWriter, background);
        background.remove("anchor");
        if (background.isEmpty())
            return;

        if (slide == null)
            layout.put(0, json.toString(background));
        else
            slide.put("background", background);
    }

    private void parseShapes(List<XSLFShape> xslfSlides, MediaWriter mediaWriter, JSONArray shapes, Map<Integer, String> layout) {
        xslfSlides.forEach(xslfShape -> {
            if (xslfShape instanceof XSLFSimpleShape) {
                JSONObject shape = copyOrNew(layout, xslfShape.getShapeId());
                parsers.parse((XSLFSimpleShape) xslfShape, mediaWriter, shape);
                if (shapes == null)
                    layout.put(xslfShape.getShapeId(), json.toString(shape));
                else
                    shapes.add(shape);

                return;
            }

            if (xslfShape instanceof XSLFGroupShape) {
                parseShapes(((XSLFGroupShape) xslfShape).getShapes(), mediaWriter, shapes, layout);

                return;
            }

            logger.warn(null, "无法处理的PPTX图形[{}]。", xslfShape);
        });
    }

    private JSONObject copyOrNew(Map<Integer, String> layout, int id) {
        return layout.containsKey(id) ? json.toObject(layout.get(id)) : new JSONObject();
    }
}
