package org.lpw.tephra.workbench.ui;

/**
 * 搜索类型。
 *
 * @author lpw
 */
public enum SearchType {
    /**
     * 不可搜索。
     */
    Null(""),
    /**
     * 等于。
     */
    Equals("=?"),
    /**
     * 大于。
     */
    Greater(">?"),
    /**
     * 大于等于。
     */
    GreaterEquals(">=?"),
    /**
     * 小于。
     */
    Less("<?"),
    /**
     * 小于等于。
     */
    LessEquals("<=?"),
    /**
     * Like。
     */
    Like(" like ?"),
    /**
     * 介于。
     */
    Between(" between ? and ?");

    private String type;

    SearchType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
