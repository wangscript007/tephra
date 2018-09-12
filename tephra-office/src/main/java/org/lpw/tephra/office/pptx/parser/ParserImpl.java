package org.lpw.tephra.office.pptx.parser;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xslf.usermodel.XSLFGraphicFrame;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSimpleShape;
import org.lpw.tephra.bean.BeanFactory;
import org.lpw.tephra.bean.ContextRefreshedListener;
import org.lpw.tephra.office.pptx.ReaderContext;
import org.lpw.tephra.office.pptx.WriterContext;
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
    public void parseShape(ReaderContext readerContext, XSLFSimpleShape xslfSimpleShape, JSONObject shape) {
        simples.forEach(simple -> simple.parseShape(readerContext, xslfSimpleShape, shape));
    }

    @Override
    public void parseShape(ReaderContext readerContext, XSLFGraphicFrame xslfGraphicFrame, JSONObject shape) {
        graphics.forEach(graphic -> graphic.parseShape(readerContext, xslfGraphicFrame, shape));
    }

    @Override
    public XSLFShape createShape(WriterContext writerContext, JSONObject shape) {
        XSLFShape xslfShape;
        for (Simple simple : simples)
            if ((xslfShape = simple.createShape(writerContext, shape)) != null)
                return xslfShape;

        for (Graphic graphic : graphics)
            if ((xslfShape = graphic.createShape(writerContext, shape)) != null)
                return xslfShape;

        return null;
    }

    @Override
    public void parseShape(WriterContext writerContext, XSLFSimpleShape xslfSimpleShape, JSONObject shape) {
        simples.forEach(simple -> simple.parseShape(writerContext, xslfSimpleShape, shape));
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
