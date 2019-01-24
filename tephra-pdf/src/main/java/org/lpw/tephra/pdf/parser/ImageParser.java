package org.lpw.tephra.pdf.parser;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.contentstream.operator.DrawObject;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.color.SetNonStrokingColor;
import org.apache.pdfbox.contentstream.operator.color.SetNonStrokingColorN;
import org.apache.pdfbox.contentstream.operator.color.SetNonStrokingColorSpace;
import org.apache.pdfbox.contentstream.operator.color.SetNonStrokingDeviceCMYKColor;
import org.apache.pdfbox.contentstream.operator.color.SetNonStrokingDeviceGrayColor;
import org.apache.pdfbox.contentstream.operator.color.SetNonStrokingDeviceRGBColor;
import org.apache.pdfbox.contentstream.operator.color.SetStrokingColor;
import org.apache.pdfbox.contentstream.operator.color.SetStrokingColorN;
import org.apache.pdfbox.contentstream.operator.color.SetStrokingColorSpace;
import org.apache.pdfbox.contentstream.operator.color.SetStrokingDeviceCMYKColor;
import org.apache.pdfbox.contentstream.operator.color.SetStrokingDeviceGrayColor;
import org.apache.pdfbox.contentstream.operator.color.SetStrokingDeviceRGBColor;
import org.apache.pdfbox.contentstream.operator.graphics.LineTo;
import org.apache.pdfbox.contentstream.operator.state.Concatenate;
import org.apache.pdfbox.contentstream.operator.state.Restore;
import org.apache.pdfbox.contentstream.operator.state.Save;
import org.apache.pdfbox.contentstream.operator.state.SetFlatness;
import org.apache.pdfbox.contentstream.operator.state.SetGraphicsStateParameters;
import org.apache.pdfbox.contentstream.operator.state.SetLineCapStyle;
import org.apache.pdfbox.contentstream.operator.state.SetLineDashPattern;
import org.apache.pdfbox.contentstream.operator.state.SetLineJoinStyle;
import org.apache.pdfbox.contentstream.operator.state.SetLineMiterLimit;
import org.apache.pdfbox.contentstream.operator.state.SetLineWidth;
import org.apache.pdfbox.contentstream.operator.state.SetMatrix;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.util.Matrix;
import org.lpw.tephra.pdf.MediaType;
import org.lpw.tephra.pdf.MediaWriter;
import org.lpw.tephra.pdf.PdfHelper;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * @author lpw
 */
public class ImageParser extends PDFStreamEngine {
    private PdfHelper pdfHelper;
    private MediaWriter mediaWriter;
    private PDPage pdPage;
    private int pageHeight;
    private JSONArray array;
    private Geometry geometry;

    public ImageParser(PdfHelper pdfHelper, MediaWriter mediaWriter, PDPage pdPage, int pageHeight) {
        super();

        this.pdfHelper = pdfHelper;
        this.mediaWriter = mediaWriter;
        this.pdPage = pdPage;
        this.pageHeight = pageHeight;
        array = new JSONArray();
        geometry = new Geometry();

        addOperator(new Concatenate());
        addOperator(new DrawObject());
        addOperator(new Save());
        addOperator(new Restore());
        addOperator(new SetFlatness());
        addOperator(new SetGraphicsStateParameters());
        addOperator(new SetLineCapStyle());
        addOperator(new SetLineDashPattern());
        addOperator(new SetLineJoinStyle());
        addOperator(new SetLineMiterLimit());
        addOperator(new SetLineWidth());
        addOperator(new SetMatrix());
        addOperator(new SetNonStrokingColor());
        addOperator(new SetNonStrokingColorN());
        addOperator(new SetNonStrokingColorSpace());
        addOperator(new SetNonStrokingDeviceCMYKColor());
        addOperator(new SetNonStrokingDeviceGrayColor());
        addOperator(new SetNonStrokingDeviceRGBColor());
        addOperator(new SetStrokingColor());
        addOperator(new SetStrokingColorN());
        addOperator(new SetStrokingColorSpace());
        addOperator(new SetStrokingDeviceCMYKColor());
        addOperator(new SetStrokingDeviceGrayColor());
        addOperator(new SetStrokingDeviceRGBColor());
    }

    @Override
    protected void processOperator(Operator operator, List<COSBase> operands) throws IOException {
        super.processOperator(operator, operands);

        String name = operator.getName();
        if (name.equals("Do"))
            image(operands);
        else
            geometry(name, operands);
    }

    private void image(List<COSBase> operands) throws IOException {
        COSName cosName = findName(operands);
        if (cosName == null)
            return;

        PDXObject pdxObject = getResources().getXObject(cosName);
        if (!(pdxObject instanceof PDImageXObject))
            return;

        Matrix matrix = getGraphicsState().getCurrentTransformationMatrix();
        JSONObject object = new JSONObject();
        JSONObject anchor = new JSONObject();
        anchor.put("width", pdfHelper.pointToPixel(matrix.getScalingFactorX()));
        int height = pdfHelper.pointToPixel(matrix.getScalingFactorY());
        anchor.put("height", height);
        anchor.put("x", pdfHelper.pointToPixel(matrix.getTranslateX()));
        anchor.put("y", pageHeight - height - pdfHelper.pointToPixel(matrix.getTranslateY()));
        object.put("anchor", anchor);

        JSONObject image = new JSONObject();
        JSONObject size = new JSONObject();
        PDImageXObject pdImageXObject = (PDImageXObject) pdxObject;
        size.put("width", pdImageXObject.getWidth());
        size.put("height", pdImageXObject.getHeight());
        image.put("size", size);

        MediaType mediaType = pdImageXObject.getSuffix().equals("png") ? MediaType.Png : MediaType.Jpeg;
        image.put("contentType", mediaType.getContentType());
        image.put("url", getUrl(pdImageXObject, mediaWriter, mediaType));
        object.put("image", image);
        array.add(object);
    }

    private COSName findName(List<COSBase> operands) {
        for (COSBase cosBase : operands)
            if (cosBase instanceof COSName)
                return (COSName) cosBase;

        return null;
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

    private void geometry(String name, List<COSBase> operands) throws IOException {
        if (name.equals("m") && operands.size() == 2)
            geometry.add(Geometry.Type.MoveTo, point(operands));
        else if ((name.equals("l") || name.equals("h")) && operands.size() == 2)
            geometry.add(Geometry.Type.LineTo, point(operands));
        else if (name.equals("c") && operands.size() == 6)
            geometry.add(Geometry.Type.CurveTo, point(operands));
        else if (name.equalsIgnoreCase("q") && operands.size() == 4)
            geometry.add(Geometry.Type.QuadTo, point(operands));
        else if (name.equals("re") && operands.size() == 4) {
            double[] points = new double[4];
            float x = floatValue(operands.get(0));
            float y = floatValue(operands.get(1));
            transform(points, 0, x, y);
            transform(points, 2, x + floatValue(operands.get(2)), y + floatValue(operands.get(3)));
            geometry.add(Geometry.Type.Rectangle, points);
        } else if (name.equalsIgnoreCase("f") )
            draw(true, false);
        else if (name.equals("S"))
            draw(false, true);
        else if (name.equals("B") )
            draw(true, true);
        else if (name.equals("n"))
            geometry.clear();
        else
            System.out.println(name);
    }

    private double[] point(List<COSBase> operands) {
        double[] points = new double[operands.size()];
        for (int i = 0, size = operands.size(); i < size; i += 2)
            transform(points, i, floatValue(operands.get(i)), floatValue(operands.get(i + 1)));

        return points;
    }

    private float floatValue(COSBase cosBase) {
        return cosBase instanceof COSNumber ? ((COSNumber) cosBase).floatValue() : 0.0F;
    }

    private void transform(double[] points, int index, float x, float y) {
        Point2D.Float point = transformedPoint(x, y);
        AffineTransform pageTransform = createCurrentPageTransformation();
        Point2D.Float transformedPoint = (Point2D.Float) pageTransform.transform(point, null);
        points[index] = transformedPoint.getX();
        points[index + 1] = transformedPoint.getY();
    }

    private AffineTransform createCurrentPageTransformation() {
        PDRectangle cropBox = pdPage.getCropBox();
        AffineTransform affineTransform = new AffineTransform();

        switch (pdPage.getRotation()) {
            case 90:
                affineTransform.translate(cropBox.getHeight(), 0);
                break;
            case 180:
                affineTransform.translate(cropBox.getWidth(), cropBox.getHeight());
                break;
            case 270:
                affineTransform.translate(0, cropBox.getWidth());
                break;
        }

        affineTransform.rotate(Math.toRadians(pdPage.getRotation()));
        affineTransform.translate(0, cropBox.getHeight());
        affineTransform.scale(1, -1);
        affineTransform.translate(-cropBox.getLowerLeftX(), -cropBox.getLowerLeftY());

        return affineTransform;
    }

    private void draw(boolean fill, boolean stroke) throws IOException {
        geometry.draw(mediaWriter, fill ? pdfHelper.toColor(getGraphicsState().getNonStrokingColor().getComponents()) : null,
                stroke ? pdfHelper.toColor(getGraphicsState().getStrokingColor().getComponents()) : null);
        if (geometry.getUrl() != null) {
            JSONObject object = new JSONObject();
            object.put("geometry", geometry.getUrl());
            JSONObject anchor = new JSONObject();
            anchor.put("x", pdfHelper.pointToPixel(geometry.getX()));
            anchor.put("y", pdfHelper.pointToPixel(geometry.getY()));
            anchor.put("width", pdfHelper.pointToPixel(geometry.getWidth()));
            anchor.put("height", pdfHelper.pointToPixel(geometry.getHeight()));
            object.put("anchor", anchor);
            array.add(object);
        }
        geometry.clear();
    }

    public JSONArray getArray() {
        return array;
    }
}
