package org.lpw.tephra.office.pptx.parser;

/**
 * @author lpw
 */
public interface Parsers {
    /**
     * 解析器类型。
     */
    enum Type {
        Presentation,
        Rels,
        Slide
    }

    /**
     * 获取解析器。
     *
     * @param type 类型。
     * @return 解析器。
     */
    Parser get(Type type);
}
