package org.lpw.tephra.poi;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFAutoShape;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSimpleShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextBox;
import org.lpw.tephra.poi.pptx.Parser;
import org.lpw.tephra.poi.pptx.ParserHelper;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Numeric;
import org.lpw.tephra.util.Validator;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * @author lpw
 */
@Component("tephra.poi.pptx")
public class PptxImpl implements Pptx {
    @Inject
    private Validator validator;
    @Inject
    private Numeric numeric;
    @Inject
    private Logger logger;
    @Inject
    private ParserHelper parserHelper;

    @Override
    public void write(JSONObject object, OutputStream outputStream) {
        if (validator.isEmpty(object) || !object.containsKey("slides"))
            return;

        XMLSlideShow xmlSlideShow = new XMLSlideShow();
        setSize(xmlSlideShow, object);
        slides(xmlSlideShow, object.getJSONArray("slides"));

        try {
            xmlSlideShow.write(outputStream);
        } catch (IOException e) {
            logger.warn(e, "输出PPTx到输出流时发生异常！");
        }
    }

    private void setSize(XMLSlideShow xmlSlideShow, JSONObject object) {
        if (!object.containsKey("size"))
            return;

        JSONObject size = object.getJSONObject("size");
        if (size.getIntValue("width") <= 0 || size.getIntValue("height") <= 0)
            return;

        xmlSlideShow.setPageSize(new Dimension(size.getIntValue("width"), size.getIntValue("height")));
    }

    private void slides(XMLSlideShow xmlSlideShow, JSONArray slides) {
        for (int i = 0, size = slides.size(); i < size; i++)
            elements(xmlSlideShow, xmlSlideShow.createSlide(), slides.getJSONObject(i).getJSONArray("elements"));
    }

    private void elements(XMLSlideShow xmlSlideShow, XSLFSlide xslfSlide, JSONArray elements) {
        for (int i = 0, size = elements.size(); i < size; i++) {
            JSONObject element = elements.getJSONObject(i);
            if (!element.containsKey("type"))
                continue;

            Parser parser = parserHelper.get(element.getString("type"));
            if (parser != null)
                parser.parse(xmlSlideShow, xslfSlide, element);
        }
    }

    @Override
    public JSONObject read(InputStream inputStream, StreamWriter streamWriter) {
        JSONObject object = new JSONObject();
        try {
            XMLSlideShow xmlSlideShow = new XMLSlideShow(inputStream);
            JSONObject size = new JSONObject();
            size.put("width", xmlSlideShow.getPageSize().width);
            size.put("height", xmlSlideShow.getPageSize().height);
            object.put("size", size);

            JSONArray slides = new JSONArray();
            slides(slides, xmlSlideShow.getSlides(), streamWriter);
            object.put("slides", slides);
            xmlSlideShow.close();
            inputStream.close();
        } catch (IOException e) {
            logger.warn(e, "解析PPTx数据时发生异常！");
        }

        return object;
    }

    private void slides(JSONArray slides, List<XSLFSlide> xslfSlides, StreamWriter streamWriter) {
        xslfSlides.forEach(xslfSlide -> {
            JSONArray elements = new JSONArray();
            xslfSlide.getShapes().forEach(xslfShape -> {
                JSONObject element = new JSONObject();
                getAnchor(element, xslfShape);
                if (xslfShape instanceof XSLFSimpleShape)
                    getRotation(element, (XSLFSimpleShape) xslfShape);
                if (xslfShape instanceof XSLFTextBox)
                    parserHelper.get(Parser.TYPE_TEXT).parse(element, xslfShape, streamWriter);
                else if (xslfShape instanceof XSLFPictureShape)
                    parserHelper.get(Parser.TYPE_IMAGE).parse(element, xslfShape, streamWriter);
                else if (xslfShape instanceof XSLFAutoShape)
                    background(element, xslfShape, streamWriter);
                elements.add(element);
            });

            JSONObject slide = new JSONObject();
            slide.put("elements", elements);
            slides.add(slide);
        });
    }

    private void getAnchor(JSONObject object, XSLFShape xslfShape) {
        Rectangle2D rectangle2D = xslfShape.getAnchor();
        object.put("x", numeric.toInt(rectangle2D.getX()));
        object.put("y", numeric.toInt(rectangle2D.getY()));
        object.put("width", numeric.toInt(rectangle2D.getWidth()));
        object.put("height", numeric.toInt(rectangle2D.getHeight()));
    }

    private void getRotation(JSONObject object, XSLFSimpleShape xslfSimpleShape) {
        if (xslfSimpleShape.getRotation() != 0.0D)
            object.put("rotation", numeric.toInt(xslfSimpleShape.getRotation()));
        if (xslfSimpleShape.getFlipVertical())
            object.put("rotationX", true);
        if (xslfSimpleShape.getFlipHorizontal())
            object.put("rotationY", true);
    }

    private void background(JSONObject object, XSLFShape xslfShape, StreamWriter streamWriter) {
        XSLFAutoShape xslfAutoShape = (XSLFAutoShape) xslfShape;
        if (!(xslfAutoShape.getFillStyle().getPaint() instanceof PaintStyle.TexturePaint))
            return;

        PaintStyle.TexturePaint texturePaint = (PaintStyle.TexturePaint) xslfAutoShape.getFillStyle().getPaint();
        try {
            InputStream inputStream = texturePaint.getImageData();
            object.put(Parser.TYPE_IMAGE, streamWriter.write(texturePaint.getContentType(), "", inputStream));
            inputStream.close();
        } catch (IOException e) {
            logger.warn(e, "保存图片[{}]流数据时发生异常！", texturePaint.getContentType());
        }
    }
}
