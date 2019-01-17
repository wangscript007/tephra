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
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.util.Matrix;
import org.lpw.tephra.pdf.MediaType;
import org.lpw.tephra.pdf.MediaWriter;
import org.lpw.tephra.pdf.PdfHelper;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.List;

/**
 * @author lpw
 */
public class ImageParser extends PDFStreamEngine {
    private JSONArray array;
    private PdfHelper pdfHelper;
    private MediaWriter mediaWriter;

    public ImageParser(PdfHelper pdfHelper, MediaWriter mediaWriter) {
        super();

        array = new JSONArray();
        this.pdfHelper = pdfHelper;
        this.mediaWriter = mediaWriter;

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
        if (!name.equals("Do"))
            return;

        COSName cosName = findName(operands);
        if (cosName == null)
            return;

        PDXObject pdxObject = getResources().getXObject(cosName);
        if (!(pdxObject instanceof PDImageXObject))
            return;

        PDImageXObject pdImageXObject = (PDImageXObject) pdxObject;
        Matrix matrix = getGraphicsState().getCurrentTransformationMatrix();
        JSONObject object = new JSONObject();
        JSONObject anchor = new JSONObject();
        anchor.put("x", pdfHelper.pointToPixel(matrix.getTranslateX()));
        anchor.put("y", pdfHelper.pointToPixel(matrix.getTranslateY()));
        anchor.put("width", pdImageXObject.getWidth());
        anchor.put("height", pdImageXObject.getHeight());
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

    private COSName findName(List<COSBase> operands) {
        for (COSBase cosBase : operands)
            if (cosBase instanceof COSName)
                return (COSName) cosBase;

        return null;
    }

    private String getUrl(PDImageXObject pdImageXObject, MediaWriter mediaWriter, MediaType mediaType) throws IOException {
        PipedOutputStream outputStream = new PipedOutputStream();
        PipedInputStream inputStream = new PipedInputStream();
        inputStream.connect(outputStream);
        ImageIO.write(pdImageXObject.getImage(), pdImageXObject.getSuffix().toUpperCase(), outputStream);
        String url = mediaWriter.write(mediaType, "pdf." + pdImageXObject.getSuffix(), inputStream);
        inputStream.close();
        outputStream.close();

        return url;
    }

    public JSONArray getArray() {
        return array;
    }
}
