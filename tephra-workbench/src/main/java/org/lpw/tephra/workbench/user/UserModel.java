package org.lpw.tephra.workbench.user;

import org.lpw.tephra.dao.model.Jsonable;
import org.lpw.tephra.workbench.Suffix;
import org.lpw.tephra.workbench.model.DomainModelSupport;
import org.lpw.tephra.workbench.model.InitableModel;
import org.lpw.tephra.workbench.ui.MenuDefinition;
import org.lpw.tephra.workbench.ui.PropertyDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * @author lpw
 */
@Component(UserModel.NAME + Suffix.MODEL)
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Entity(name = UserModel.NAME)
@Table(name = "t_tephra_workbench_user")
@MenuDefinition(sort = 2)
public class UserModel extends DomainModelSupport implements InitableModel {
    public static final String NAME = "tephra.workbench.user";

    private String username;
    private String password;
    private String realname;
    private Timestamp register;

    @Jsonable
    @PropertyDefinition(sort = 1)
    @Column(name = "c_username")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(name = "c_password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Jsonable
    @PropertyDefinition(sort = 2)
    @Column(name = "c_realname")
    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
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

    @Override
    public void init() {
        register = new Timestamp(System.currentTimeMillis());
    }
}
