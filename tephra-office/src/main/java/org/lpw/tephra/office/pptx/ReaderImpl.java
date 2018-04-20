package org.lpw.tephra.office.pptx;

import com.alibaba.fastjson.JSONObject;
import org.lpw.tephra.bean.BeanFactory;
import org.lpw.tephra.bean.ContextRefreshedListener;
import org.lpw.tephra.office.OfficeHelper;
import org.lpw.tephra.util.Image;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Zipper;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lpw
 */
@Component("tephra.office.pptx.reader")
public class ReaderImpl implements Reader, ContextRefreshedListener {
    @Inject
    private Zipper zipper;
    @Inject
    private Image image;
    @Inject
    private Logger logger;
    @Inject
    private OfficeHelper officeHelper;
    private Map<String, Parser> parsers;

    @Override
    public void read(InputStream inputStream) {
        try {
            String path = unzip(inputStream);
            JSONObject size = parsers.get("presentation").parse(path + "/ppt/presentation.xml", null);
            JSONObject images = images(path);
            Parser rels = parsers.get("rels");
            JSONObject slide = parsers.get("slide").parse(path + "/ppt/slides/slide1.xml",
                    rels.parse(path + "/ppt/slides/_rels/slide1.xml.rels", images));
            System.out.println(size);
            System.out.println(slide);
        } catch (Exception e) {
            logger.warn(e, "读取PPTX信息时发生异常！");
        }
    }

    private String unzip(InputStream inputStream) throws IOException {
        String output = officeHelper.getTempPath("pptx");
        zipper.unzip(inputStream, new File(output));

        return output;
    }

    private JSONObject images(String path) {
        JSONObject object = new JSONObject();
        File[] files = new File(path + "/ppt/media/").listFiles();
        if (files == null)
            return object;

        for (File file : files) {
            int[] size = image.size(file);
            if (size == null || size.length != 2)
                continue;

            JSONObject obj = new JSONObject();
            String uri = "/ppt/media/" + file.getName();
            obj.put("uri", uri);
            obj.put("width", size[0]);
            obj.put("height", size[1]);
            object.put(uri, obj);
        }

        return object;
    }

    @Override
    public int getContextRefreshedSort() {
        return 9;
    }

    @Override
    public void onContextRefreshed() {
        parsers = new HashMap<>();
        BeanFactory.getBeans(Parser.class).forEach(parser -> parsers.put(parser.getName(), parser));
    }
}
