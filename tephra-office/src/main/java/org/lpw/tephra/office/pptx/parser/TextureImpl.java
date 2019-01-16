package org.lpw.tephra.office.pptx.parser;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSimpleShape;
import org.apache.xmlbeans.XmlObject;
import org.lpw.tephra.office.MediaType;
import org.lpw.tephra.office.OfficeHelper;
import org.lpw.tephra.office.pptx.ReaderContext;
import org.lpw.tephra.office.pptx.WriterContext;
import org.lpw.tephra.util.Image;
import org.lpw.tephra.util.Logger;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlipFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTRelativeRect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTStretchInfoProperties;
import org.openxmlformats.schemas.presentationml.x2006.main.CTBackground;
import org.openxmlformats.schemas.presentationml.x2006.main.CTShape;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author lpw
 */
@Component("tephra.office.pptx.parser.texture")
public class TextureImpl implements Simple {
    @Inject
    private Image image;
    @Inject
    private Logger logger;
    @Inject
    private OfficeHelper officeHelper;

    @Override
    public int getSort() {
        return 4;
    }

    @Override
    public void parseShape(ReaderContext readerContext, XSLFSimpleShape xslfSimpleShape, JSONObject shape) {
        PaintStyle paintStyle = xslfSimpleShape.getFillStyle().getPaint();
        if (!(paintStyle instanceof PaintStyle.TexturePaint))
            return;

        try {
            JSONObject texture = new JSONObject();
            PaintStyle.TexturePaint texturePaint = (PaintStyle.TexturePaint) paintStyle;
            InputStream inputStream = texturePaint.getImageData();
            int[] wh = image.size(inputStream);
            inputStream.reset();
            JSONObject size = new JSONObject();
            size.put("width", wh[0]);
            size.put("height", wh[1]);
            texture.put("size", size);

            texture.put("contentType", texturePaint.getContentType());
            texture.put("alpha", texturePaint.getAlpha() / 100000.0D);
            texture.put("url", readerContext.getMediaWriter().write(MediaType.find(texturePaint.getContentType()),
                    null, texturePaint.getImageData()));
            parseFillRect(xslfSimpleShape, texture);
            shape.put("texture", texture);
        } catch (IOException e) {
            logger.warn(e, "解析填充图片时发生异常！");
        }
    }

    private void parseFillRect(XSLFSimpleShape xslfSimpleShape, JSONObject texture) {
        XmlObject xmlObject = xslfSimpleShape.getXmlObject();
        if (xmlObject instanceof CTBackground)
            parseBlipFill(((CTBackground) xmlObject).getBgPr().getBlipFill(), texture);
        else if (xmlObject instanceof CTShape)
            parseBlipFill(((CTShape) xmlObject).getSpPr().getBlipFill(), texture);
    }

    private void parseBlipFill(CTBlipFillProperties ctBlipFillProperties, JSONObject texture) {
        CTStretchInfoProperties ctStretchInfoProperties = ctBlipFillProperties.getStretch();
        if (ctStretchInfoProperties == null)
            return;

        CTRelativeRect ctRelativeRect = ctStretchInfoProperties.getFillRect();
        if (ctRelativeRect == null)
            return;

        texture.put("left", officeHelper.fromPercent(ctRelativeRect.getL()));
        texture.put("top", officeHelper.fromPercent(ctRelativeRect.getT()));
        texture.put("right", officeHelper.fromPercent(ctRelativeRect.getR()));
        texture.put("bottom", officeHelper.fromPercent(ctRelativeRect.getB()));
    }

    @Override
    public void parseShape(WriterContext writerContext, XSLFSimpleShape xslfSimpleShape, JSONObject shape) {
    }

    @Override
    public XSLFShape createShape(WriterContext writerContext, JSONObject shape) {
        return null;
    }
}
