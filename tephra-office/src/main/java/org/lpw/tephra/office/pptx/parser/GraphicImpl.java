package org.lpw.tephra.office.pptx.parser;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xslf.usermodel.XSLFGraphicFrame;
import org.apache.poi.xslf.usermodel.XSLFTable;
import org.lpw.tephra.office.pptx.MediaWriter;
import org.springframework.stereotype.Component;

/**
 * @author lpw
 */
@Component("tephra.office.pptx.parser.graphic")
public class GraphicImpl implements Graphic {
    @Override
    public int getSort() {
        return 12;
    }

    @Override
    public void parse(XSLFGraphicFrame xslfGraphicFrame, MediaWriter mediaWriter, JSONObject shape) {
        if (xslfGraphicFrame instanceof XSLFTable)
            return;

        System.out.println("###############################################");
        System.out.println(xslfGraphicFrame.getXmlObject());
    }
}
