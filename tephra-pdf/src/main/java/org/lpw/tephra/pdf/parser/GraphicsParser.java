package org.lpw.tephra.pdf.parser;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.pdfbox.contentstream.PDFGraphicsStreamEngine;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.graphics.image.PDImage;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.util.Matrix;
import org.lpw.tephra.pdf.MediaType;
import org.lpw.tephra.pdf.MediaWriter;
import org.lpw.tephra.pdf.PdfHelper;
import org.w3c.dom.Element;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Image;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lpw
 */
public class GraphicsParser extends PDFGraphicsStreamEngine {
    private PdfHelper pdfHelper;
    private MediaWriter mediaWriter;
    private float width;
    private float height;
    private List<String> types;
    private Map<Integer, double[]> points;
    private Point2D point2D;
    private JSONArray array;

    public GraphicsParser(PDPage pdPage, PdfHelper pdfHelper, MediaWriter mediaWriter) {
        super(pdPage);

        this.pdfHelper = pdfHelper;
        this.mediaWriter = mediaWriter;
        width = pdPage.getCropBox().getWidth();
        height = pdPage.getCropBox().getHeight();
        types = new ArrayList<>();
        points = new HashMap<>();
        array = new JSONArray();
    }

    @Override
    public void appendRectangle(Point2D p0, Point2D p1, Point2D p2, Point2D p3) throws IOException {
        addPoints((float) p3.getX(), (float) p3.getY(), (float) p1.getX(), (float) p1.getY());
        types.add("rect");
    }

    @Override
    public void drawImage(PDImage pdImage) throws IOException {
        if (types.isEmpty())
            image((PDImageXObject) pdImage);
        else
            draw(false, false, (PDImageXObject) pdImage);
        reset();
    }

    private void image(PDImageXObject pdImageXObject) throws IOException {
        Matrix matrix = getGraphicsState().getCurrentTransformationMatrix();
        JSONObject object = new JSONObject();
        JSONObject anchor = new JSONObject();
        anchor.put("x", pdfHelper.pointToPixel(matrix.getTranslateX()));
        anchor.put("y", pdfHelper.pointToPixel(height - matrix.getScalingFactorY() - matrix.getTranslateY()));
        anchor.put("width", pdfHelper.pointToPixel(matrix.getScalingFactorX()));
        anchor.put("height", pdfHelper.pointToPixel(matrix.getScalingFactorY()));
        object.put("anchor", anchor);

        JSONObject image = new JSONObject();
        JSONObject size = new JSONObject();
        size.put("width", pdImageXObject.getWidth());
        size.put("height", pdImageXObject.getHeight());
        image.put("size", size);

        MediaType mediaType = pdImageXObject.getSuffix().equals("png") ? MediaType.Png : MediaType.Jpeg;
        image.put("contentType", mediaType.getContentType());
        image.put("url", getUrl(pdImageXObject, mediaWriter, mediaType));
        object.put("image", image);
        array.add(object);
    }

    private String getUrl(PDImageXObject pdImageXObject, MediaWriter mediaWriter, MediaType mediaType) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(pdImageXObject.getImage(), pdImageXObject.getSuffix().toUpperCase(), outputStream);
        outputStream.close();

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        String url = mediaWriter.write(mediaType, "pdf." + pdImageXObject.getSuffix(), inputStream);
        inputStream.close();

        return url;
    }

    @Override
    public void clip(int windingRule) throws IOException {
        types.add("clip");
    }

    @Override
    public void moveTo(float x, float y) throws IOException {
        addPoints(x, y);
        types.add("move-to");
        point2D = new Point2D.Float(x, y);
    }

    @Override
    public void lineTo(float x, float y) throws IOException {
        addPoints(x, y);
        types.add("line-to");
    }

    @Override
    public void curveTo(float x1, float y1, float x2, float y2, float x3, float y3) throws IOException {
        addPoints(x1, y1, x2, y2, x3, y3);
        types.add("curve-to");
    }

    private void addPoints(float... fs) {
        double[] ds = new double[fs.length];
        for (int i = 0; i < fs.length; i += 2) {
            ds[i] = fs[i];
            ds[i + 1] = height - fs[i + 1];
        }
        points.put(types.size(), ds);
    }

    @Override
    public Point2D getCurrentPoint() throws IOException {
        return point2D;
    }

    @Override
    public void closePath() throws IOException {
        types.add("close");
    }

    @Override
    public void endPath() throws IOException {
        if (types.size() == 2 && types.get(0).equals("rect") && types.get(1).equals("clip") && full())
            reset();
        else
            types.add("end");
    }

    @Override
    public void strokePath() throws IOException {
        if (types.isEmpty())
            return;

        draw(false, true, null);
        reset();
    }

    @Override
    public void fillPath(int windingRule) throws IOException {
        if (types.isEmpty())
            return;

        if (types.size() == 1 && types.get(0).equals("rect") && full()) {
            Color color = pdfHelper.toColor(getGraphicsState().getNonStrokingColor().getComponents());
            if (color.getRed() == 255 && color.getGreen() == 255 && color.getBlue() == 255) {
                reset();

                return;
            }
        }

        draw(true, false, null);
        reset();
    }

    private boolean full() {
        double[] point = points.get(0);

        return equals(point[0], 0.0D) && equals(point[1], 0.0D) && equals(point[2], width) && equals(point[3], height);
    }

    @Override
    public void fillAndStrokePath(int windingRule) throws IOException {
        if (types.isEmpty())
            return;

        draw(true, true, null);
        reset();
    }

    private void draw(boolean fill, boolean stroke, PDImageXObject pdImageXObject) throws IOException {
        SVGGraphics2D svgGraphics2D = new SVGGraphics2D(GenericDOMImplementation.getDOMImplementation()
                .createDocument(SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", null));
        double[] anchor = getAnchor();
        Path2D.Double path = getPath(anchor);
        if (fill) {
            Color color = pdfHelper.toColor(getGraphicsState().getNonStrokingColor().getComponents());
            if (color != null) {
                svgGraphics2D.setColor(color);
                svgGraphics2D.fill(path);
            }
        }
        if (stroke) {
            Color color = pdfHelper.toColor(getGraphicsState().getStrokingColor().getComponents());
            if (color != null) {
                svgGraphics2D.setColor(color);
                svgGraphics2D.draw(path);
            }
        }
        if (pdImageXObject != null) {
            Matrix matrix = getGraphicsState().getCurrentTransformationMatrix();
            int w = (int) matrix.getScalingFactorX();
            int h = (int) matrix.getScalingFactorY();
            svgGraphics2D.clip(path);
            svgGraphics2D.drawImage(pdImageXObject.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH),
                    (int) (matrix.getTranslateX() - anchor[0]), (int) (matrix.getTranslateY() - anchor[1]), w, h, null);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Element root = svgGraphics2D.getRoot();
        root.setAttribute("viewBox", "0 0 " + anchor[2] + " " + anchor[3]);
        svgGraphics2D.stream(root, new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), false, false);
        svgGraphics2D.dispose();
        outputStream.flush();
        outputStream.close();

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toString().trim()
                .replaceAll("\\s+", " ")
                .replaceAll(" >", ">")
                .replaceAll("> <", "><")
                .replaceAll("<g [^>]+></g>", "").getBytes());
        String url = mediaWriter.write(MediaType.Svg, "geometry.svg", inputStream);
        inputStream.close();
        if (url == null)
            return;

        JSONObject object = new JSONObject();
        object.put("geometry", url);
        JSONObject obj = new JSONObject();
        obj.put("x", pdfHelper.pointToPixel(anchor[0]));
        obj.put("y", pdfHelper.pointToPixel(anchor[1]));
        obj.put("width", pdfHelper.pointToPixel(anchor[2]));
        obj.put("height", pdfHelper.pointToPixel(anchor[3]));
        object.put("anchor", obj);
        array.add(object);
    }

    private Path2D.Double getPath(double[] anchor) {
        Path2D.Double path = new Path2D.Double();
        for (int i = 0, size = types.size(); i < size; i++) {
            double[] point = points.get(i);
            switch (types.get(i)) {
                case "move-to":
                    path.moveTo(point[0] - anchor[0], point[1] - anchor[1]);
                    break;
                case "line-to":
                    path.lineTo(point[0] - anchor[0], point[1] - anchor[1]);
                    break;
                case "curve-to":
                    path.curveTo(point[0] - anchor[0], point[1] - anchor[1],
                            point[2] - anchor[0], point[3] - anchor[1],
                            point[4] - anchor[0], point[5] - anchor[1]);
                    break;
                case "rect":
//                    if (i == 0 && size > 1 && types.get(1).equals("clip") && equals(point[0], anchor[0]) && equals(point[1], anchor[1])
//                            && equals(point[2], anchor[0] + anchor[2]) && equals(point[3], anchor[1] + anchor[3])) {
//                        i++;
//
//                        break;
//                    }

                    path.moveTo(point[0] - anchor[0], point[1] - anchor[1]);
                    path.lineTo(point[2] - anchor[0], point[1] - anchor[1]);
                    path.lineTo(point[2] - anchor[0], point[3] - anchor[1]);
                    path.lineTo(point[0] - anchor[0], point[3] - anchor[1]);
                    path.lineTo(point[0] - anchor[0], point[1] - anchor[1]);
                    break;
                case "close":
                    path.closePath();
                    break;
            }
        }

        return path;
    }

    private double[] getAnchor() {
        double[] anchor = new double[]{Double.MAX_VALUE, Double.MAX_VALUE, 0.0D, 0.0D};
        for (double[] ds : points.values()) {
            for (int i = 0; i < ds.length; i += 2) {
                anchor[0] = Math.min(anchor[0], ds[i]);
                anchor[1] = Math.min(anchor[1], ds[i + 1]);
                anchor[2] = Math.max(anchor[2], ds[i]);
                anchor[3] = Math.max(anchor[3], ds[i + 1]);
            }
        }
        anchor[2] -= anchor[0];
        anchor[3] -= anchor[1];

        return anchor;
    }

    private boolean equals(double d1, double d2) {
        return Math.abs(d1 - d2) < 0.1D;
    }

    @Override
    public void shadingFill(COSName shadingName) throws IOException {
    }

    private void reset() {
        types.clear();
        points.clear();
    }

    public JSONArray getArray() {
        return array;
    }
}
