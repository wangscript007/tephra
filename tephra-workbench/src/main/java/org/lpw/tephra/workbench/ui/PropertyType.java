package org.lpw.tephra.workbench.ui;

/**
 * @author lpw
 */
public enum PropertyType {
    /**
     * 文本。
     */
    Text("text"),
    /**
     * 密码。
     */
    Password("password"),
    /**
     * 日期。
     */
    Date("date");

    private String name;

    PropertyType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
