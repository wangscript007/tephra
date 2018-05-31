package org.lpw.tephra.office.pptx.parser;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xslf.usermodel.XSLFGraphicFrame;
import org.apache.poi.xslf.usermodel.XSLFSimpleShape;
import org.lpw.tephra.bean.BeanFactory;
import org.lpw.tephra.bean.ContextRefreshedListener;
import org.lpw.tephra.office.MediaWriter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author lpw
 */
@Component("tephra.office.pptx.parser")
public class ParserImpl implements Parser, ContextRefreshedListener {
    private List<Simple> simples;
    private List<Graphic> graphics;

    @Override
    public void parse(XSLFSimpleShape xslfSimpleShape, MediaWriter mediaWriter, JSONObject shape, boolean layout) {
        simples.forEach(simple -> simple.parse(xslfSimpleShape, mediaWriter, shape, layout));
    }

    @Override
    public void parse(XSLFGraphicFrame xslfGraphicFrame, MediaWriter mediaWriter, JSONObject shape) {
        graphics.forEach(graphic -> graphic.parse(xslfGraphicFrame, mediaWriter, shape));
    }

    @Override
    public int getContextRefreshedSort() {
        return 9;
    }

    @Override
    public void onContextRefreshed() {
        simples = new ArrayList<>();
        simples.addAll(BeanFactory.getBeans(Simple.class));
        simples.sort(Comparator.comparingInt(Simple::getSort));
//        simples.forEach(simple -> System.out.println(simple.getSort() + ";" + simple));

        graphics = new ArrayList<>();
        graphics.addAll(BeanFactory.getBeans(Graphic.class));
        graphics.sort(Comparator.comparingInt(Graphic::getSort));
    }
}
