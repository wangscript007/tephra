package org.lpw.tephra.workbench.ui;

/**
 * 属性。
 *
 * @author lpw
 */
public class Property implements Comparable<Property> {
    private String name;
    private PropertyDefinition definition;

    public Property(String name, PropertyDefinition definition) {
        this.name = name;
        this.definition = definition;
    }

    public String getName() {
        return name;
    }

    public PropertyDefinition getDefinition() {
        return definition;
    }

    @Override
    public int compareTo(Property o) {
        return definition.sort() - o.definition.sort();
    }
}
