package org.lpw.tephra.office.pptx.parser;

import com.alibaba.fastjson.JSONObject;
import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSimpleShape;
import org.lpw.tephra.office.MediaType;
import org.lpw.tephra.office.OfficeHelper;
import org.lpw.tephra.office.pptx.ReaderContext;
import org.lpw.tephra.office.pptx.WriterContext;
import org.lpw.tephra.util.Logger;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;

import javax.inject.Inject;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * @author lpw
 */
@Component("tephra.office.pptx.parser.geometry")
public class GeometryImpl implements Simple {
    @Inject
    private Logger logger;
    @Inject
    private OfficeHelper officeHelper;
    @Inject
    private Set<Zero> zeros;

    @Override
    public int getSort() {
        return 5;
    }

    @Override
    public void parseShape(ReaderContext readerContext, XSLFSimpleShape xslfSimpleShape, JSONObject shape) {
        if (xslfSimpleShape.getLineWidth() == 0.0D && xslfSimpleShape.getLineColor() == null && xslfSimpleShape.getFillColor() == null)
            return;

        zeros.forEach(zero -> zero.zero(xslfSimpleShape, shape));
        try {
            shape.put("geometry", save(readerContext, xslfSimpleShape));
        } catch (Exception e) {
            logger.warn(e, "保存图形[{}]为SVG图片文件时发生异常！", xslfSimpleShape);
        }
        zeros.forEach(zero -> zero.reset(xslfSimpleShape, shape));
    }

    private String save(ReaderContext readerContext, XSLFSimpleShape xslfSimpleShape) throws IOException {
        SVGGraphics2D svgGraphics2D = new SVGGraphics2D(GenericDOMImplementation.getDOMImplementation()
                .createDocument(SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", null));
        Rectangle2D rectangle2D = xslfSimpleShape.getAnchor();
        xslfSimpleShape.draw(svgGraphics2D, new Rectangle2D.Double(0.0D, 0.0D, rectangle2D.getWidth(), rectangle2D.getHeight()));
        Element root = svgGraphics2D.getRoot();
        root.setAttribute("viewBox", "0 0 " + rectangle2D.getWidth() + " " + rectangle2D.getHeight());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        svgGraphics2D.stream(root, new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), false, false);
        svgGraphics2D.dispose();
        outputStream.flush();
        outputStream.close();

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toString().trim()
                .replaceAll("\\s+", " ")
                .replaceAll(" >", ">")
                .replaceAll("> <", "><")
                .replaceAll("<text [^>]+>[^<]*</text>", "")
                .replaceAll("<g [^>]+></g>", "").getBytes());
        String image = readerContext.getMediaWriter().write(MediaType.SVG, "geometry.svg", inputStream);
        inputStream.close();

        return image;
    }

    @Override
    public XSLFShape createShape(WriterContext writerContext, JSONObject shape) {
        return null;
    }

    @Override
    public void parseShape(WriterContext writerContext, XSLFSimpleShape xslfSimpleShape, JSONObject shape) {
        if (!shape.containsKey("geometry"))
            return;

        JSONObject geometry = shape.getJSONObject("geometry");
        parseFillToShape(xslfSimpleShape, geometry);
    }

    private void parseFillToShape(XSLFSimpleShape xslfSimpleShape, JSONObject geometry) {
        if (!geometry.containsKey("fill"))
            return;

        JSONObject fill = geometry.getJSONObject("fill");
        if (fill.containsKey("color"))
            xslfSimpleShape.setFillColor(officeHelper.jsonToColor(fill.getJSONObject("color")));
    }
}
