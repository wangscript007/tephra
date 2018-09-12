package org.lpw.tephra.office.pptx;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.lpw.tephra.office.MediaReader;

/**
 * 写上下文。
 *
 * @author lpw
 */
public class WriterContext {
    private MediaReader mediaReader;
    private XMLSlideShow xmlSlideShow;
    private XSLFSlide xslfSlide;

    public WriterContext(MediaReader mediaReader, XMLSlideShow xmlSlideShow) {
        this.mediaReader = mediaReader;
        this.xmlSlideShow = xmlSlideShow;
    }

    public MediaReader getMediaReader() {
        return mediaReader;
    }

    public XMLSlideShow getXmlSlideShow() {
        return xmlSlideShow;
    }

    public XSLFSlide getXslfSlide() {
        return xslfSlide;
    }

    public void setXslfSlide(XSLFSlide xslfSlide) {
        this.xslfSlide = xslfSlide;
    }
}
