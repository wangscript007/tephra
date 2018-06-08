package org.lpw.tephra.office.pptx;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFBackground;
import org.apache.poi.xslf.usermodel.XSLFGraphicFrame;
import org.apache.poi.xslf.usermodel.XSLFGroupShape;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSimpleShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.lpw.tephra.office.MediaWriter;
import org.lpw.tephra.office.OfficeHelper;
import org.lpw.tephra.office.pptx.parser.Parser;
import org.lpw.tephra.util.Json;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Numeric;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGroupTransform2D;
import org.openxmlformats.schemas.presentationml.x2006.main.CTGroupShape;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.awt.Dimension;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lpw
 */
@Component("tephra.office.pptx.reader")
public class PptxReaderImpl implements PptxReader {
    @Inject
    private Json json;
    @Inject
    private Numeric numeric;
    @Inject
    private Logger logger;
    @Inject
    private OfficeHelper officeHelper;
    @Inject
    private Parser parser;

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
        size.put("width", officeHelper.pointToPixel(dimension.width));
        size.put("height", officeHelper.pointToPixel(dimension.height));
        object.put("size", size);
    }

    private Map<Integer, String> parseLayout(XSLFSlide xslfSlide, MediaWriter mediaWriter, Map<String, Map<Integer, String>> layouts) {
        String name = xslfSlide.getSlideLayout().getName();
        if (layouts.containsKey(name))
            return layouts.get(name);

        Map<Integer, String> layout = new LinkedHashMap<>();
        parseBackground(xslfSlide.getSlideLayout().getBackground(), mediaWriter, null, layout);
        parseShapes(xslfSlide, xslfSlide.getSlideLayout().getShapes(), mediaWriter, null, layout, new HashMap<>());
        layouts.put(name, layout);

        return layout;
    }

    private void parseSlide(XSLFSlide xslfSlide, MediaWriter mediaWriter, JSONObject slide, Map<Integer, String> layout) {
        parseBackground(xslfSlide.getBackground(), mediaWriter, slide, layout);

        JSONArray shapes = new JSONArray();
        Map<Integer, JSONObject> fromLayout = new HashMap<>();
        layout.forEach((id, shape) -> {
            if (id == 0)
                return;

            JSONObject object = json.toObject(shape);
            shapes.add(object);
            fromLayout.put(id, object);
        });
        parseShapes(xslfSlide, xslfSlide.getShapes(), mediaWriter, shapes, layout, fromLayout);
        slide.put("shapes", shapes);
    }

    private void parseBackground(XSLFBackground xslfBackground, MediaWriter mediaWriter, JSONObject slide,
                                 Map<Integer, String> layout) {
        JSONObject background = layout.containsKey(0) ? json.toObject(layout.get(0)) : new JSONObject();
        parser.parse(xslfBackground, mediaWriter, background, slide == null);
        background.remove("anchor");
        if (background.isEmpty())
            return;

        if (slide == null)
            layout.put(0, json.toString(background));
        else
            slide.put("background", background);
    }

    private void parseShapes(XSLFSlide xslfSlide, List<XSLFShape> xslfSlides, MediaWriter mediaWriter, JSONArray shapes,
                             Map<Integer, String> layout, Map<Integer, JSONObject> fromLayout) {
        xslfSlides.forEach(xslfShape -> {
            if (xslfShape instanceof XSLFSimpleShape) {
                JSONObject shape = getOrNew(fromLayout, xslfShape.getShapeId());
                parser.parse((XSLFSimpleShape) xslfShape, mediaWriter, shape, shapes == null);
                if (shapes == null)
                    layout.put(xslfShape.getShapeId(), json.toString(shape));
                else
                    shapes.add(shape);

                return;
            }

            if (xslfShape instanceof XSLFGraphicFrame) {
                JSONObject shape = getOrNew(fromLayout, xslfShape.getShapeId());
                parser.parse(xslfSlide, (XSLFGraphicFrame) xslfShape, mediaWriter, shape);
                shapes.add(shape);

                return;
            }

            if (xslfShape instanceof XSLFGroupShape) {
                if (shapes != null)
                    parseGroup(xslfSlide, (XSLFGroupShape) xslfShape, mediaWriter, shapes, layout, fromLayout);

                return;
            }

            logger.warn(null, "无法处理的PPTX图形[{}]。", xslfShape);
        });
    }

    private void parseGroup(XSLFSlide xslfSlide, XSLFGroupShape xslfGroupShape, MediaWriter mediaWriter, JSONArray shapes,
                            Map<Integer, String> layout, Map<Integer, JSONObject> fromLayout) {
        CTGroupShape ctGroupShape = (CTGroupShape) xslfGroupShape.getXmlObject();
        CTGroupTransform2D ctGroupTransform2D = ctGroupShape.getGrpSpPr().getXfrm();
        int chX = officeHelper.emuToPixel(ctGroupTransform2D.getChOff().getX() - ctGroupTransform2D.getOff().getX());
        int chY = officeHelper.emuToPixel(ctGroupTransform2D.getChOff().getY() - ctGroupTransform2D.getOff().getY());
        double chW = 1.0D * ctGroupTransform2D.getExt().getCx() / ctGroupTransform2D.getChExt().getCx();
        double chH = 1.0D * ctGroupTransform2D.getExt().getCy() / ctGroupTransform2D.getChExt().getCy();

        JSONArray array = new JSONArray();
        parseShapes(xslfSlide, xslfGroupShape.getShapes(), mediaWriter, array, layout, fromLayout);
        for (int i = 0, size = array.size(); i < size; i++) {
            JSONObject object = array.getJSONObject(i);
            JSONObject anchor = object.getJSONObject("anchor");
            anchor.put("x", anchor.getIntValue("x") - chX);
            anchor.put("y", anchor.getIntValue("y") - chY);
            anchor.put("width", numeric.toInt(anchor.getIntValue("width") * chW));
            anchor.put("height", numeric.toInt(anchor.getIntValue("height") * chH));
            shapes.add(object);
        }
    }

    private JSONObject getOrNew(Map<Integer, JSONObject> layout, int id) {
        return layout.containsKey(id) ? layout.get(id) : new JSONObject();
    }
}
