package org.lpw.tephra.office.pptx.parser;

/**
 * 解析器。
 *
 * @author lpw
 */
public interface Parser {
    /**
     * 获取解析器类型。
     *
     * @return 解析器类型。
     */
    Parsers.Type getType();

    /**
     * 解析。
     *
     * @param path         文件路径。
     * @param parserObject 解析数据。
     */
    void parse(String path, ParserObject parserObject);
}
