package org.lpw.tephra.pdf;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.lpw.tephra.pdf.parser.GraphicsParser;
import org.lpw.tephra.pdf.parser.TextParser;
import org.lpw.tephra.util.Io;
import org.lpw.tephra.util.Logger;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lpw
 */
@Component("tephra.pdf.reader")
public class PdfReaderImpl implements PdfReader {
    @Inject
    private Io io;
    @Inject
    private Logger logger;
    @Inject
    private PdfHelper pdfHelper;

    @Override
    public JSONObject read(InputStream inputStream, MediaWriter mediaWriter) {
        JSONObject object = new JSONObject();
        JSONArray pages = new JSONArray();
        int pageHeight = 0;
        try (PDDocument pdDocument = PDDocument.load(inputStream)) {
            int size = pdDocument.getNumberOfPages();
            if (size == 0)
                return object;

            for (int i = 0; i < size; i++) {
                PDPage pdPage = pdDocument.getPage(i);
                if (i == 0) {
                    parseSize(object, pdPage);
                    pageHeight = object.getJSONObject("size").getIntValue("height");
                }

                JSONArray elements = new JSONArray();
                parseImage(elements, pdPage, mediaWriter);
                parseText(elements, pdDocument, pageHeight, i);

                JSONObject page = new JSONObject();
                page.put("elements", elements);
                pages.add(page);
            }

        } catch (IOException e) {
            logger.warn(e, "解析PDF数据时发生异常！");
        }

        object.put("pages", pages);

        return object;
    }

    private void parseSize(JSONObject object, PDPage pdPage) {
        JSONObject size = new JSONObject();
        size.put("width", pdfHelper.pointToPixel(pdPage.getCropBox().getWidth()));
        size.put("height", pdfHelper.pointToPixel(pdPage.getCropBox().getHeight()));
        object.put("size", size);
    }

    private void parseImage(JSONArray elements, PDPage pdPage, MediaWriter mediaWriter) throws IOException {
        GraphicsParser graphicsParser = new GraphicsParser(pdPage, pdfHelper, mediaWriter);
        graphicsParser.processPage(pdPage);
        merge(elements, graphicsParser.getArray());
    }

    private void parseText(JSONArray elements, PDDocument pdDocument, int pageHeight, int page) throws IOException {
        TextParser textParser = new TextParser(pdfHelper, pageHeight);
        textParser.setStartPage(page + 1);
        textParser.setEndPage(page + 1);
        textParser.getText(pdDocument);
        merge(elements, textParser.getArray());
    }

    private void merge(JSONArray elements, JSONArray array) {
        if (!array.isEmpty())
            elements.addAll(array);
    }

    @Override
    public String png(InputStream inputStream, MediaWriter mediaWriter, int page) {
        String url = null;
        try (PipedInputStream pipedInputStream = new PipedInputStream(); PipedOutputStream pipedOutputStream = new PipedOutputStream();
             PDDocument document = PDDocument.load(inputStream)) {
            pipedOutputStream.connect(pipedInputStream);
            ImageIO.write(new PDFRenderer(document).renderImage(page, 1.0f, ImageType.RGB), "PNG", pipedOutputStream);
            url = mediaWriter.write(MediaType.Png, page + ".png", pipedInputStream);
        } catch (IOException e) {
            logger.warn(e, "读取PDF为图片时发生异常！");
        }

        return url;
    }

    @Override
    public List<String> pngs(InputStream inputStream, MediaWriter mediaWriter) {
        List<String> list = new ArrayList<>();
        try (PDDocument document = PDDocument.load(inputStream)) {
            PDFRenderer renderer = new PDFRenderer(document);
            BufferedImage together = null;
            for (int i = 0, size = document.getNumberOfPages(); i < size; i++) {
                BufferedImage bufferedImage = renderer.renderImage(i, 1.0f, ImageType.RGB);
                list.add(write(mediaWriter, bufferedImage, i + ".png"));

                if (together == null)
                    together = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight() * size, BufferedImage.TYPE_4BYTE_ABGR);
                together.getGraphics().drawImage(bufferedImage, 0, i * bufferedImage.getHeight(), null);
            }

            if (together != null)
                list.add(0, write(mediaWriter, together, "together.png"));
        } catch (IOException e) {
            logger.warn(e, "读取PDF为图片时发生异常！");
        }

        return list;
    }

    private String write(MediaWriter mediaWriter, BufferedImage bufferedImage, String fileName) throws IOException {
        PipedInputStream pipedInputStream = new PipedInputStream();
        PipedOutputStream pipedOutputStream = new PipedOutputStream();
        pipedOutputStream.connect(pipedInputStream);
        ImageIO.write(bufferedImage, "PNG", pipedOutputStream);
        String url = mediaWriter.write(MediaType.Png, fileName, pipedInputStream);
        pipedOutputStream.close();
        pipedInputStream.close();

        return url;
    }
}
