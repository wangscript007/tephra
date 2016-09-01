package org.lpw.tephra.dao.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Model支持类，主键ID由数据库自行维护。
 *
 * @author lpw
 */
@MappedSuperclass()
public class NativeIdModelSupport implements Model {
    private static final String ID = "c_id";
    private static final String NATIVE = "native";

    private String id;

    @Jsonable
    @Column(name = ID)
    @Id
    @GeneratedValue(generator = NATIVE)
    @GenericGenerator(name = NATIVE, strategy = NATIVE)
    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }
}
