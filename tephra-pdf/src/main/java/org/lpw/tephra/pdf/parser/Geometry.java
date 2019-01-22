package org.lpw.tephra.pdf.parser;

import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.lpw.tephra.pdf.MediaType;
import org.lpw.tephra.pdf.MediaWriter;
import org.w3c.dom.Element;

import java.awt.Color;
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
        Line
    }

    private List<Type> types;
    private List<int[]> p1s;
    private List<int[]> p2s;
    private int x;
    private int y;
    private int maxX;
    private int maxY;
    private int width;
    private int height;
    private String url;

    Geometry() {
        types = new ArrayList<>();
        p1s = new ArrayList<>();
        p2s = new ArrayList<>();
    }

    void add(Type type, int[] p1, int[] p2) {
        types.add(type);
        p1s.add(new int[]{p1[0], p1[1]});
        p2s.add(new int[]{p2[0], p2[1]});
    }

    void draw(MediaWriter mediaWriter, Color fill) throws IOException {
        if (types.isEmpty())
            return;

        transform();

        SVGGraphics2D svgGraphics2D = new SVGGraphics2D(GenericDOMImplementation.getDOMImplementation()
                .createDocument(SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", null));
        if (fill != null)
            svgGraphics2D.setColor(fill);
        for (int i = 0, size = types.size(); i < size; i++) {
            int[] p1 = p1s.get(i);
            int[] p2 = p2s.get(i);
            switch (types.get(i)) {
                case Line:
                    svgGraphics2D.drawLine(p1[0], p1[1], p2[0], p2[1]);
                    break;
                default:
            }
        }
        Element root = svgGraphics2D.getRoot();
        root.setAttribute("viewBox", "0 0 " + width + " " + height);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
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

    private void transform() {
        x = y = Integer.MAX_VALUE;
        min(p1s);
        min(p2s);
        max(p1s);
        max(p2s);
        width = maxX - x;
        height = maxY - y;
        transofrm(p1s);
        transofrm(p2s);
    }

    private void min(List<int[]> list) {
        for (int[] point : list) {
            x = Math.min(x, point[0]);
            y = Math.min(y, point[1]);
        }
    }

    private void max(List<int[]> list) {
        for (int[] point : list) {
            maxX = Math.max(maxX, point[0]);
            maxY = Math.max(maxY, point[1]);
        }
    }

    private void transofrm(List<int[]> list) {
        for (int[] point : list) {
            point[0] -= x;
            point[1] -= y;
        }
    }

    int getX() {
        return x;
    }

    int getY() {
        return y;
    }

    int getWidth() {
        return width;
    }

    int getHeight() {
        return height;
    }

    String getUrl() {
        return url;
    }
}
