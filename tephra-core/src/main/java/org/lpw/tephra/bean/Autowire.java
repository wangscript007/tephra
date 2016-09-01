package org.lpw.tephra.bean;

import java.lang.reflect.Field;

/**
 * @author lpw
 */
public class Autowire {
    protected Object bean;
    protected Field field;
    protected boolean collection;

    public Autowire(Object bean, Field field, boolean collection) {
        this.bean = bean;
        this.field = field;
        this.collection = collection;
    }

    public Object getBean() {
        return bean;
    }

    public Field getField() {
        return field;
    }

    public boolean isCollection() {
        return collection;
    }
}
