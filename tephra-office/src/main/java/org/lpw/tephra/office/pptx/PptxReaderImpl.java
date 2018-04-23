package org.lpw.tephra.office.pptx;

import org.lpw.tephra.office.OfficeHelper;
import org.lpw.tephra.office.pptx.parser.Parser;
import org.lpw.tephra.office.pptx.parser.ParserObject;
import org.lpw.tephra.office.pptx.parser.Parsers;
import org.lpw.tephra.util.Image;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Zipper;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author lpw
 */
@Component("tephra.office.pptx.reader")
public class PptxReaderImpl implements PptxReader {
    @Inject
    private Zipper zipper;
    @Inject
    private Image image;
    @Inject
    private Logger logger;
    @Inject
    private OfficeHelper officeHelper;
    @Inject
    private Parsers parsers;

    @Override
    public void read(InputStream inputStream) {
        ParserObject parserObject = new ParserObject();
        try {
            String path = unzip(inputStream);
            parsers.get(Parsers.Type.Presentation).parse(path + "/ppt/presentation.xml", parserObject);
            images(path, parserObject);
            Parser rels = parsers.get(Parsers.Type.Rels);
            Parser slide = parsers.get(Parsers.Type.Slide);
            for (int i = 1; i <= 1; i++) {
                rels.parse(path + "/ppt/slides/_rels/slide" + i + ".xml.rels", parserObject);
                slide.parse(path + "/ppt/slides/slide1" + i + ".xml", parserObject);
            }
        } catch (Exception e) {
            logger.warn(e, "读取PPTX信息时发生异常！");
        }
        
        System.out.println("#######################################################");
        System.out.println(parserObject.getWidth() + "," + parserObject.getHeight());
        parserObject.getSlides().forEach(System.out::println);
    }

    private String unzip(InputStream inputStream) throws IOException {
        String output = officeHelper.getTempPath("pptx");
        zipper.unzip(inputStream, new File(output));

        return output;
    }

    private void images(String path, ParserObject parserObject) {
        File[] files = new File(path + "/ppt/media/").listFiles();
        if (files == null)
            return;

        for (File file : files) {
            int[] size = image.size(file);
            if (size == null || size.length != 2)
                continue;

            parserObject.addImage("/ppt/media/" + file.getName(), size);
        }
    }
}
