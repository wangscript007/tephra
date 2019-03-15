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
    private double[] area;
    private List<String> clipTypes;
    private Map<Integer, double[]> clipPoints;
    private double[] clipArea;
    private Point2D point2D;
    private JSONArray array;

    public GraphicsParser(PDPage pdPage, PdfHelper pdfHelper, MediaWriter mediaWriter) {
        super(pdPage);

        this.pdfHelper = pdfHelper;
        this.mediaWriter = mediaWriter;
        width = pdPage.getCropBox().getWidth();
        height = pdPage.getCropBox().getHeight();
        array = new JSONArray();
        reset();
        resetClip();
    }

    @Override
    public void appendRectangle(Point2D p0, Point2D p1, Point2D p2, Point2D p3) throws IOException {
        addPoints((float) p0.getX(), (float) p0.getY(), (float) p2.getX(), (float) p2.getY());
        double[] point = points.get(types.size());
        area(point, point[0], point[1]);
        area(point, point[2], point[3]);
        types.add("rect");
    }

    @Override
    public void drawImage(PDImage pdImage) throws IOException {
//        if (clipTypes.isEmpty())
        image((PDImageXObject) pdImage);
//        else
//            draw(false, false, (PDImageXObject) pdImage);
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
        clipTypes = types;
        clipPoints = points;
        clipArea = area;
        reset();
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
            area(area, ds[i], ds[i + 1]);
        }
        points.put(types.size(), ds);
    }

    private void area(double[] area, double x, double y) {
        area[0] = Math.min(area[0], x);
        area[1] = Math.min(area[1], y);
        area[2] = Math.max(area[2], x);
        area[3] = Math.max(area[3], y);
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
    }

    @Override
    public void strokePath() throws IOException {
        if (types.isEmpty())
            return;

        draw(area, false, true);
        reset();
    }

    @Override
    public void fillPath(int windingRule) throws IOException {
        if (types.isEmpty())
            return;

        if (types.size() == 1 && types.get(0).equals("rect") && full(points.get(0))) {
            Color color = pdfHelper.toColor(getGraphicsState().getNonStrokingColor().getComponents());
            System.out.println(color);
            if (color.getRed() == 255 && color.getGreen() == 255 && color.getBlue() == 255) {
                reset();

                return;
            }
        }

        draw(area, true, false);
        reset();
    }

    private boolean full(double[] point) {
        return point[0] <= 0.1D && point[1] <= 0.1D && point[2] >= width - 0.1D && point[3] >= height - 0.1D;
    }

    @Override
    public void fillAndStrokePath(int windingRule) throws IOException {
        if (types.isEmpty())
            return;

        draw(area, true, true);
        reset();
    }

    @Override
    public void shadingFill(COSName shadingName) throws IOException {
    }

    private void draw(double[] area, boolean fill, boolean stroke) throws IOException {
        double w = area[2] - area[0];
        double h = area[3] - area[1];
        if (area[0] >= width - 0.1D || area[1] >= height - 0.1D || w <= 1.0D || h <= 1.0D)
            return;

        SVGGraphics2D svgGraphics2D = new SVGGraphics2D(GenericDOMImplementation.getDOMImplementation()
                .createDocument(SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", null));
        Path2D.Double path = getPath(types, points, area);
        if (fill) {
            Color color = pdfHelper.toColor(getGraphicsState().getNonStrokingColor().getComponents());
            if (color != null) {
                svgGraphics2D.setColor(color);
                svgGraphics2D.fill(path);
            } else
                fill = false;
        }
        if (stroke) {
            Color color = pdfHelper.toColor(getGraphicsState().getStrokingColor().getComponents());
            if (color != null) {
                svgGraphics2D.setColor(color);
                svgGraphics2D.draw(path);
            } else
                stroke = false;
        }
        if (!fill && !stroke)
            return;

//        if (pdImageXObject != null) {
//            Matrix matrix = getGraphicsState().getCurrentTransformationMatrix();
//            int w = (int) matrix.getScalingFactorX();
//            int h = (int) matrix.getScalingFactorY();
//            svgGraphics2D.clip(getPath(clipTypes,clipPoints,clipArea));
//            svgGraphics2D.drawImage(pdImageXObject.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH),
//                    (int) (matrix.getTranslateX() - clipArea[0]), (int) (matrix.getTranslateY() - clipArea[1]), w, h, null);
//        }

        save(svgGraphics2D, area[0], area[1], w, h);
    }

    private Path2D.Double getPath(List<String> types, Map<Integer, double[]> points, double[] area) {
        Path2D.Double path = new Path2D.Double();
        for (int i = 0, size = types.size(); i < size; i++) {
            double[] point = points.get(i);
            switch (types.get(i)) {
                case "move-to":
                    path.moveTo(point[0] - area[0], point[1] - area[1]);
                    break;
                case "line-to":
                    path.lineTo(point[0] - area[0], point[1] - area[1]);
                    break;
                case "curve-to":
                    path.curveTo(point[0] - area[0], point[1] - area[1],
                            point[2] - area[0], point[3] - area[1],
                            point[4] - area[0], point[5] - area[1]);
                    break;
                case "rect":
                    path.moveTo(point[0] - area[0], point[1] - area[1]);
                    path.lineTo(point[2] - area[0], point[1] - area[1]);
                    path.lineTo(point[2] - area[0], point[3] - area[1]);
                    path.lineTo(point[0] - area[0], point[3] - area[1]);
                    path.lineTo(point[0] - area[0], point[1] - area[1]);
                    break;
                case "close":
                    path.closePath();
                    break;
            }
        }

        return path;
    }

    private void save(SVGGraphics2D svgGraphics2D, double x, double y, double width, double height) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Element root = svgGraphics2D.getRoot();
        root.setAttribute("viewBox", "0 0 " + width + " " + height);
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
        JSONObject anchor = new JSONObject();
        anchor.put("x", pdfHelper.pointToPixel(x));
        anchor.put("y", pdfHelper.pointToPixel(y));
        anchor.put("width", pdfHelper.pointToPixel(width));
        anchor.put("height", pdfHelper.pointToPixel(height));
        object.put("anchor", anchor);
        array.add(object);
    }

    private void reset() {
        types = new ArrayList<>();
        points = new HashMap<>();
        area = new double[]{Double.MAX_VALUE, Double.MAX_VALUE, 0.0D, 0.0D};
    }

    private void resetClip() {
        clipTypes = new ArrayList<>();
        clipPoints = new HashMap<>();
        clipArea = new double[]{Double.MAX_VALUE, Double.MAX_VALUE, 0.0D, 0.0D};
    }

    public JSONArray getArray() {
        return array;
    }
}
