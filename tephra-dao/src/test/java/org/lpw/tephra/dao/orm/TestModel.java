package org.lpw.tephra.dao.orm;

import org.lpw.tephra.dao.model.Memory;
import org.lpw.tephra.dao.model.ModelSupport;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author lpw
 */
@Component("tephra.dao.orm.model")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Entity(name = "tephra.dao.orm")
@Table(name = "t_tephra_test")
public class TestModel extends ModelSupport {
    private int sort;
    private String name;

    @Column(name = "c_sort")
    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    @Column(name = "c_name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
