package org.lpw.tephra.pdf.parser;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.contentstream.operator.DrawObject;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.state.Concatenate;
import org.apache.pdfbox.contentstream.operator.state.Restore;
import org.apache.pdfbox.contentstream.operator.state.Save;
import org.apache.pdfbox.contentstream.operator.state.SetGraphicsStateParameters;
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
    private int[] startPoint;
    private int[] prevPoint;

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
        addOperator(new SetGraphicsStateParameters());
        addOperator(new Save());
        addOperator(new Restore());
        addOperator(new SetMatrix());
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
            startPoint = prevPoint = point(operands);
        else if (name.equals("l") && operands.size() == 2) {
            int[] point = point(operands);
            geometry.add(Geometry.Type.Line, prevPoint, point);
            prevPoint = point;
        } else if (name.equals("h") && operands.size() == 2) {
            int[] point = point(operands);
            geometry.add(Geometry.Type.Line, point, startPoint);
            prevPoint = point;
        } else if (name.equals("f") || name.equals("F") || name.equals("f*"))
            draw(true, false);
    }

    private int[] point(List<COSBase> operands) {
        Point2D.Float point = super.transformedPoint(((COSNumber) operands.get(0)).floatValue(), ((COSNumber) operands.get(1)).floatValue());
        AffineTransform pageTransform = createCurrentPageTransformation();
        Point2D.Float transformedPoint = (Point2D.Float) pageTransform.transform(point, null);

        return new int[]{pdfHelper.pointToPixel(transformedPoint.getX()), pdfHelper.pointToPixel(transformedPoint.getY())};
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
        geometry.draw(mediaWriter, fill ? null : pdfHelper.toColor(getGraphicsState().getNonStrokingColor().getComponents()));
        if (geometry.getUrl() != null) {
            JSONObject object = new JSONObject();
            object.put("geometry", geometry.getUrl());
            JSONObject anchor = new JSONObject();
            anchor.put("x", geometry.getX());
            anchor.put("y", geometry.getY());
            anchor.put("width", geometry.getWidth());
            anchor.put("height", geometry.getHeight());
            object.put("anchor", anchor);
            array.add(object);
            System.out.println(object);
        }
        geometry = new Geometry();
    }

    public JSONArray getArray() {
        return array;
    }
}
