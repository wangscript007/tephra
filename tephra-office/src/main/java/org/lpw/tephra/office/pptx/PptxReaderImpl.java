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
import org.lpw.tephra.office.MediaType;
import org.lpw.tephra.office.MediaWriter;
import org.lpw.tephra.office.OfficeHelper;
import org.lpw.tephra.office.pptx.parser.Parser;
import org.lpw.tephra.util.Json;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Numeric;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
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
        try (XMLSlideShow xmlSlideShow = new XMLSlideShow(inputStream)) {
            parseSize(xmlSlideShow, object);
            ReaderContext readerContext = new ReaderContext(mediaWriter, xmlSlideShow);
            Map<String, Map<Integer, String>> layouts = new HashMap<>();
            JSONArray slides = new JSONArray();
            xmlSlideShow.getSlides().forEach(xslfSlide -> {
                readerContext.setXslfSlide(xslfSlide);
                JSONObject slide = new JSONObject();
                parseSlide(readerContext, slide, parseLayout(readerContext, layouts));
                slides.add(slide);
            });
            object.put("slides", slides);
            inputStream.close();
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

    private Map<Integer, String> parseLayout(ReaderContext readerContext, Map<String, Map<Integer, String>> layouts) {
        readerContext.setLayout(true);
        String name = readerContext.getXslfSlide().getSlideLayout().getName();
        if (layouts.containsKey(name))
            return layouts.get(name);

        Map<Integer, String> layout = new LinkedHashMap<>();
        parseBackground(readerContext, readerContext.getXslfSlide().getSlideLayout().getBackground(), null, layout);
        parseShapes(readerContext, readerContext.getXslfSlide().getSlideLayout().getShapes(), null, layout, new HashMap<>());
        layouts.put(name, layout);

        return layout;
    }

    private void parseSlide(ReaderContext readerContext, JSONObject slide, Map<Integer, String> layout) {
        readerContext.setLayout(false);
        parseBackground(readerContext, readerContext.getXslfSlide().getBackground(), slide, layout);

        Map<Integer, JSONObject> fromLayout = new HashMap<>();
        layout.forEach((id, shape) -> {
            if (id == 0)
                return;

            fromLayout.put(id, json.toObject(shape));
        });
        JSONArray shapes = new JSONArray();
        parseShapes(readerContext, readerContext.getXslfSlide().getShapes(), shapes, layout, fromLayout);
        slide.put("shapes", shapes);
    }

    private void parseBackground(ReaderContext readerContext, XSLFBackground xslfBackground, JSONObject slide,
                                 Map<Integer, String> layout) {
        JSONObject background = layout.containsKey(0) ? json.toObject(layout.get(0)) : new JSONObject();
        parser.parseShape(readerContext, xslfBackground, background);
        background.remove("anchor");

        if (xslfBackground.getFillColor() != null)
            background.put("color", officeHelper.colorToJson(xslfBackground.getFillColor()));

        if (background.isEmpty())
            return;

        if (readerContext.isLayout())
            layout.put(0, json.toString(background));
        else
            slide.put("background", background);
    }

    private void parseShapes(ReaderContext readerContext, List<XSLFShape> xslfShapes, JSONArray shapes,
                             Map<Integer, String> layout, Map<Integer, JSONObject> fromLayout) {
        xslfShapes.forEach(xslfShape -> {
            if (xslfShape instanceof XSLFSimpleShape) {
                JSONObject shape = getOrNew(fromLayout, xslfShape.getShapeId());
                parser.parseShape(readerContext, (XSLFSimpleShape) xslfShape, shape);
                if (readerContext.isLayout())
                    layout.put(xslfShape.getShapeId(), json.toString(shape));
                else
                    add(shapes, shape);

                return;
            }

            if (xslfShape instanceof XSLFGraphicFrame) {
                JSONObject shape = getOrNew(fromLayout, xslfShape.getShapeId());
                parser.parseShape(readerContext, (XSLFGraphicFrame) xslfShape, shape);
                add(shapes, shape);

                return;
            }

            if (xslfShape instanceof XSLFGroupShape) {
                if (shapes != null)
                    parseGroup(readerContext, (XSLFGroupShape) xslfShape, shapes, layout, fromLayout);

                return;
            }

            logger.warn(null, "无法处理的PPTX图形[{}]。", xslfShape);
        });
    }

    private void parseGroup(ReaderContext readerContext, XSLFGroupShape xslfGroupShape, JSONArray shapes,
                            Map<Integer, String> layout, Map<Integer, JSONObject> fromLayout) {
        JSONArray array = new JSONArray();
        parseShapes(readerContext, xslfGroupShape.getShapes(), array, layout, fromLayout);
        double rate = xslfGroupShape.getAnchor().getWidth() / xslfGroupShape.getInteriorAnchor().getWidth();
        for (int i = 0, size = array.size(); i < size; i++) {
            JSONObject object = array.getJSONObject(i);
            JSONObject anchor = object.getJSONObject("anchor");
            anchor.put("x", numeric.toInt(anchor.getIntValue("x") * rate));
            anchor.put("y", numeric.toInt(anchor.getIntValue("y") * rate));
            anchor.put("width", numeric.toInt(anchor.getIntValue("width") * rate));
            anchor.put("height", numeric.toInt(anchor.getIntValue("height") * rate));
            add(shapes, object);
        }
    }

    private JSONObject getOrNew(Map<Integer, JSONObject> layout, int id) {
        return layout.containsKey(id) ? layout.get(id) : new JSONObject();
    }

    private void add(JSONArray shapes, JSONObject shape) {
        if (shape.isEmpty() || (shape.size() == 1 && shape.containsKey("anchor")))
            return;

        shapes.add(shape);
    }

    @Override
    public List<String> pngs(InputStream inputStream, MediaWriter mediaWriter, boolean merge) {
        List<String> list = new ArrayList<>();
        try (XMLSlideShow xmlSlideShow = new XMLSlideShow(inputStream)) {
            Dimension dimension = xmlSlideShow.getPageSize();
            List<XSLFSlide> xslfSlides = xmlSlideShow.getSlides();
            BufferedImage together = null;
            for (int i = 0, size = xslfSlides.size(); i < size; i++) {
                BufferedImage bufferedImage = new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_4BYTE_ABGR);
                Graphics2D graphics2D = bufferedImage.createGraphics();
                xslfSlides.get(i).draw(graphics2D);
                graphics2D.dispose();
                list.add(write(mediaWriter, bufferedImage, i + ".png"));
                if (!merge)
                    continue;

                if (together == null)
                    together = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight() * size, BufferedImage.TYPE_4BYTE_ABGR);
                together.getGraphics().drawImage(bufferedImage, 0, i * bufferedImage.getHeight(), null);
            }

            if (merge && together != null)
                list.add(0, write(mediaWriter, together, "together.png"));
            inputStream.close();
        } catch (Throwable throwable) {
            logger.warn(throwable, "读取PPT为PNG图集时发生异常！");
        }

        return list;
    }

    private String write(MediaWriter mediaWriter, BufferedImage bufferedImage, String fileName) throws IOException {
        PipedInputStream pipedInputStream = new PipedInputStream();
        PipedOutputStream pipedOutputStream = new PipedOutputStream();
        pipedOutputStream.connect(pipedInputStream);
        ImageIO.write(bufferedImage, "PNG", pipedOutputStream);
        pipedOutputStream.close();
        String url = mediaWriter.write(MediaType.Png, fileName, pipedInputStream);
        pipedInputStream.close();

        return url;
    }
}
