package org.lpw.tephra.dao.orm.lite;

/**
 * @author lpw
 */
public class Index {
    public enum Type {Use, Ignore, Force}

    public enum Key {Key, Index}

    public enum For {Null, Join, Order, Group}

    private Type type;
    private Key key;
    private String name;
    private For f;

    public Index(Type type, Key key, String name, For f) {
        this.type = type;
        this.key = key;
        this.name = name;
        this.f = f;
    }

    public Type getType() {
        return type;
    }

    public Key getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public For getFor() {
        return f;
    }
}
