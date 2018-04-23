package org.lpw.tephra.office.pptx.parser;

import org.lpw.tephra.bean.BeanFactory;
import org.lpw.tephra.bean.ContextRefreshedListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lpw
 */
@Component("tephra.office.pptx.parsers")
public class ParsersImpl implements Parsers, ContextRefreshedListener {
    private Map<Type, Parser> map;

    @Override
    public Parser get(Type type) {
        return map.get(type);
    }

    @Override
    public int getContextRefreshedSort() {
        return 9;
    }

    @Override
    public void onContextRefreshed() {
        map = new HashMap<>();
        BeanFactory.getBeans(Parser.class).forEach(parser -> map.put(parser.getType(), parser));
    }
}
