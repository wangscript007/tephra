package org.lpw.tephra.office.pptx.parser;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xslf.usermodel.XSLFGraphicFrame;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFTable;
import org.apache.xmlbeans.XmlObject;
import org.lpw.tephra.office.pptx.ReaderContext;
import org.lpw.tephra.office.pptx.WriterContext;
import org.lpw.tephra.util.Io;
import org.lpw.tephra.util.Logger;
import org.openxmlformats.schemas.presentationml.x2006.main.CTGraphicalObjectFrame;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;

import javax.inject.Inject;
import java.io.IOException;

/**
 * @author lpw
 */
@Component("tephra.office.pptx.parser.graphic")
public class GraphicImpl implements Graphic {
    @Inject
    private Io io;
    @Inject
    private Logger logger;

    @Override
    public int getSort() {
        return 12;
    }

    @Override
    public void parseShape(ReaderContext readerContext, XSLFGraphicFrame xslfGraphicFrame, JSONObject shape) {
        if (xslfGraphicFrame instanceof XSLFTable)
            return;

        XmlObject xmlObject = xslfGraphicFrame.getXmlObject();
        if (!(xmlObject instanceof CTGraphicalObjectFrame))
            return;

        Node node = ((CTGraphicalObjectFrame) xmlObject).getGraphic().getGraphicData().getDomNode().getFirstChild();
        if (!node.getNodeName().equals("c:chart"))
            return;

        String rId = node.getAttributes().getNamedItem("r:id").getNodeValue();
        try {
            System.out.println(io.readAsString(readerContext.getXslfSlide().getRelationById(rId).getPackagePart().getInputStream()));
        } catch (IOException e) {
            logger.warn(e, "读取图表[{}]数据时发生异常！", xmlObject);
        }
    }

    @Override
    public XSLFShape createShape(WriterContext writerContext, JSONObject shape) {
        return null;
    }
}
