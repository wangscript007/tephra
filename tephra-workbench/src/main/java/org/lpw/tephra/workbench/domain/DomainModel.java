package org.lpw.tephra.workbench.domain;

import org.lpw.tephra.ctrl.validate.Validate;
import org.lpw.tephra.ctrl.validate.Validates;
import org.lpw.tephra.ctrl.validate.Validators;
import org.lpw.tephra.dao.model.Jsonable;
import org.lpw.tephra.workbench.Suffix;
import org.lpw.tephra.workbench.model.InitableModel;
import org.lpw.tephra.workbench.model.StatusModelSupport;
import org.lpw.tephra.workbench.ui.MenuDefinition;
import org.lpw.tephra.workbench.ui.PropertyDefinition;
import org.lpw.tephra.workbench.ui.PropertyType;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * @author lpw
 */
@Component(DomainModel.NAME + Suffix.MODEL)
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Entity(name = DomainModel.NAME)
@Table(name = "t_tephra_workbench_domain")
@MenuDefinition(parent = 99, sort = 1)
@Validates({@Validate(validator = Validators.NOT_EMPTY, parameter = "key", failureCode = 1001, failureArgKeys = {"tephra.workbench.domain.key"}),
        @Validate(validator = Validators.NOT_EMPTY, parameter = "name", failureCode = 1002, failureArgKeys = {"tephra.workbench.domain.name"})})
public class DomainModel extends StatusModelSupport implements InitableModel {
    public static final String NAME = "tephra.workbench.domain";

    private String key;
    private String name;
    private Timestamp register;
    private Date valid;

    @Jsonable
    @PropertyDefinition(sort = 1)
    @Column(name = "c_key")
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Jsonable
    @PropertyDefinition(sort = 2)
    @Column(name = "c_name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Jsonable
    @PropertyDefinition(sort = 3, editable = false)
    @Column(name = "c_register")
    public Timestamp getRegister() {
        return register;
    }

    public void setRegister(Timestamp register) {
        this.register = register;
    }

    @Jsonable
    @PropertyDefinition(sort = 4, type = PropertyType.Date)
    @Column(name = "c_valid")
    public Date getValid() {
        return valid;
    }

    public void setValid(Date valid) {
        this.valid = valid;
    }

    @Override
    public void init() {
        register = new Timestamp(System.currentTimeMillis());
        valid = new Date(System.currentTimeMillis());
    }
}
