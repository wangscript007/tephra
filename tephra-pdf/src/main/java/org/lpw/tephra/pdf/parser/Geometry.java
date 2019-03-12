package org.lpw.tephra.pdf.parser;

import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.lpw.tephra.pdf.MediaType;
import org.lpw.tephra.pdf.MediaWriter;
import org.w3c.dom.Element;

import java.awt.Color;
import java.awt.geom.Path2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lpw
 */
class Geometry {
    enum Type {
        MoveTo,
        LineTo,
        CurveTo,
        QuadTo,
        Rectangle,
        Close
    }

    private List<Type> types;
    private List<double[]> points;
    private double x;
    private double y;
    private double width;
    private double height;
    private String url;

    Geometry() {
        types = new ArrayList<>();
        points = new ArrayList<>();
    }

    void add(Type type, double[] point) {
        types.add(type);
        points.add(point);
    }

    void draw(MediaWriter mediaWriter, Color fill, Color stroke) throws IOException {
        if (types.isEmpty() || (fill == null && stroke == null))
            return;

        SVGGraphics2D svgGraphics2D = new SVGGraphics2D(GenericDOMImplementation.getDOMImplementation()
                .createDocument(SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", null));
        if (fill != null) {
            svgGraphics2D.setColor(fill);
            svgGraphics2D.fill(getPath());
        }
        if (stroke != null) {
            svgGraphics2D.setColor(stroke);
            svgGraphics2D.draw(getPath());
        }
        if (width <= 0 || height <= 0)
            return;

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
        url = mediaWriter.write(MediaType.Svg, "geometry.svg", inputStream);
        inputStream.close();
    }

    private Path2D.Double getPath() {
        transform();
        Path2D.Double path = new Path2D.Double();
        double prevX = -1;
        double prevY = -1;
        for (int i = 0, size = types.size(); i < size; i++) {
            double[] point = points.get(i);
            switch (types.get(i)) {
                case MoveTo:
                    path.moveTo(point[0], point[1]);
                    break;
                case LineTo:
                    path.lineTo(point[0], point[1]);
                    break;
                case CurveTo:
                    path.curveTo(point[0], point[1], point[2], point[3], point[4], point[5]);
                    break;
                case QuadTo:
                    path.quadTo(point[0], point[1], point[2], point[3]);
                    break;
                case Rectangle:
                    path.moveTo(point[0], point[1]);
                    path.lineTo(point[2], point[1]);
                    path.lineTo(point[2], point[3]);
                    path.lineTo(point[0], point[3]);
                    path.lineTo(point[0], point[1]);
                    break;
                case Close:
                    path.closePath();
                    break;
            }
            if (point.length > 1) {
                prevX = point[0];
                prevY = point[1];
            } else {
                prevX = -1;
                prevY = -1;
            }
        }

        return path;
    }

    private void transform() {
        x = y = Double.MAX_VALUE;
        double maxX = 0.0D;
        double maxY = 0.0D;
        for (double[] ns : points) {
            for (int i = 0; i < ns.length; i += 2) {
                x = Math.min(x, ns[i]);
                y = Math.min(y, ns[i + 1]);
                maxX = Math.max(maxX, ns[i]);
                maxY = Math.max(maxY, ns[i + 1]);
            }
        }
        for (double[] ns : points) {
            for (int i = 0; i < ns.length; i += 2) {
                ns[i] -= x;
                ns[i + 1] -= y;
            }
        }
        width = maxX - x;
        height = maxY - y;
    }

    double getX() {
        return x;
    }

    double getY() {
        return y;
    }

    double getWidth() {
        return width;
    }

    double getHeight() {
        return height;
    }

    String getUrl() {
        return url;
    }

    void clear() {
        types.clear();
        points.clear();
    }
}
