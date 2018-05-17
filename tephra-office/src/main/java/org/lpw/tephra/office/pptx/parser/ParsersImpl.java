package org.lpw.tephra.office.pptx.parser;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xslf.usermodel.XSLFSimpleShape;
import org.lpw.tephra.bean.BeanFactory;
import org.lpw.tephra.bean.ContextRefreshedListener;
import org.lpw.tephra.office.pptx.MediaWriter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author lpw
 */
@Component("tephra.office.pptx.parsers")
public class ParsersImpl implements Parsers, ContextRefreshedListener {
    private List<Parser> list;

    @Override
    public void parse(XSLFSimpleShape xslfSimpleShape, MediaWriter mediaWriter, JSONObject shape, boolean layout) {
        list.forEach(parser -> parser.parse(xslfSimpleShape, mediaWriter, shape, layout));
    }

    @Override
    public int getContextRefreshedSort() {
        return 9;
    }

    @Override
    public void onContextRefreshed() {
        list = new ArrayList<>();
        list.addAll(BeanFactory.getBeans(Parser.class));
        list.sort(Comparator.comparingInt(Parser::getSort));
    }
}
