package org.lpw.tephra.ctrl.context.local;

import org.lpw.tephra.bean.BeanFactory;
import org.lpw.tephra.ctrl.context.SessionAdapter;
import org.lpw.tephra.util.Generator;

/**
 * 基于字符串的Session适配器实现。
 *
 * @author lpw
 */
public class LocalSessionAdapter implements SessionAdapter {
    protected String id;

    public LocalSessionAdapter(String id) {
        this.id = id == null ? BeanFactory.getBean(Generator.class).random(32) : id;
    }

    @Override
    public String getId() {
        return id;
    }
}
