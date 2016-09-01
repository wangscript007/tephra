package org.lpw.tephra.workbench.ui;

import java.util.List;

/**
 * @author lpw
 */
public class Menu implements Comparable<Menu> {
    private int grade;
    private int sort;
    private String key;
    private String label;
    private String uri;
    private List<Menu> children;

    public Menu(int grade, int sort, String key, String uri) {
        this.grade = grade;
        this.sort = sort;
        this.key = key;
        this.uri = uri;
    }

    public int getGrade() {
        return grade;
    }

    public int getSort() {
        return sort;
    }

    public String getKey() {
        return key;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getUri() {
        return uri;
    }

    public List<Menu> getChildren() {
        return children;
    }

    public void setChildren(List<Menu> children) {
        this.children = children;
    }

    @Override
    public int compareTo(Menu o) {
        return grade == o.grade ? (sort - o.sort) : (grade - o.grade);
    }
}
