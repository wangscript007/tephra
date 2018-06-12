package org.lpw.tephra.office.pptx;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.lpw.tephra.office.MediaReader;
import org.lpw.tephra.util.DateTime;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Validator;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.awt.Dimension;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;

/**
 * @author lpw
 */
@Component("tephra.office.pptx.writer")
public class PptxWriterImpl implements PptxWriter {
    @Inject
    private DateTime dateTime;
    @Inject
    private Validator validator;
    @Inject
    private Logger logger;

    @Override
    public void write(OutputStream outputStream, JSONObject object, MediaReader mediaReader) {
        XMLSlideShow xmlSlideShow = newXMLSlideShow();
        parseSize(xmlSlideShow, object.getJSONObject("size"));
        parseSildes(xmlSlideShow, object.getJSONArray("slides"));

        try {
            xmlSlideShow.write(outputStream);
        } catch (IOException e) {
            logger.warn(e, "输出PPTx到流时发生异常！");
        }
    }

    private XMLSlideShow newXMLSlideShow() {
        XMLSlideShow xmlSlideShow = new XMLSlideShow();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MILLISECOND, -1 * (calendar.get(Calendar.ZONE_OFFSET) + calendar.get(Calendar.DST_OFFSET)));
        String time = dateTime.toString(calendar.getTime(), "yyyy-MM-dd'T'HH:mm:ss'Z'");
        xmlSlideShow.getProperties().getCoreProperties().setCreated(time);
        xmlSlideShow.getProperties().getCoreProperties().setModified(time);

        return xmlSlideShow;
    }

    private void parseSize(XMLSlideShow xmlSlideShow, JSONObject size) {
        if (validator.isEmpty(size))
            return;

        xmlSlideShow.setPageSize(new Dimension(size.getIntValue("width"), size.getIntValue("height")));
    }

    private void parseSildes(XMLSlideShow xmlSlideShow, JSONArray slides) {
        if (validator.isEmpty(slides))
            return;

        for (int i = 0, size = slides.size(); i < size; i++) {
            JSONObject slide = slides.getJSONObject(i);
            XSLFSlide xslfSlide = xmlSlideShow.createSlide();
        }
    }
}
