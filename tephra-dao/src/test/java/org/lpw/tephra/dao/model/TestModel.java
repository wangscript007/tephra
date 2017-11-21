package org.lpw.tephra.dao.model;

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
@Component(TestModel.NAME + ".model")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Entity(name = TestModel.NAME)
@Table(name = "t_model")
public class TestModel extends ModelSupport {
    static final String NAME = "tephra.dao.model";

    private String string;
    private int integer1;
    private long integer2;
    private float decimal1;
    private double decimal2;
    private Date date;
    private Timestamp timestamp;
    private String extend1;
    private String extend2;
    private String extend3;
    private String extend4;
    private String json;

    @Jsonable
    @Column(name = "c_string")
    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    @Jsonable
    @Column(name = "c_integer1")
    public int getInteger1() {
        return integer1;
    }

    public void setInteger1(int integer1) {
        this.integer1 = integer1;
    }

    @Jsonable
    @Column(name = "c_integer2")
    public long getInteger2() {
        return integer2;
    }

    public void setInteger2(long integer2) {
        this.integer2 = integer2;
    }

    @Jsonable
    @Column(name = "c_decimal1")
    public float getDecimal1() {
        return decimal1;
    }

    public void setDecimal1(float decimal1) {
        this.decimal1 = decimal1;
    }

    @Jsonable
    @Column(name = "c_decimal2")
    public double getDecimal2() {
        return decimal2;
    }

    public void setDecimal2(double decimal2) {
        this.decimal2 = decimal2;
    }

    @Jsonable
    @Column(name = "c_date")
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Jsonable
    @Column(name = "c_timestamp")
    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Jsonable(extend = true)
    @Column(name = "c_extend1")
    public String getExtend1() {
        return extend1;
    }

    public void setExtend1(String extend1) {
        this.extend1 = extend1;
    }

    @Jsonable(extend = true)
    @Column(name = "c_extend2")
    public String getExtend2() {
        return extend2;
    }

    public void setExtend2(String extend2) {
        this.extend2 = extend2;
    }

    @Jsonable(extend = true)
    @Column(name = "c_extend3")
    public String getExtend3() {
        return extend3;
    }

    public void setExtend3(String extend3) {
        this.extend3 = extend3;
    }

    @Jsonable
    @Column(name = "c_extend4")
    public String getExtend4() {
        return extend4;
    }

    public void setExtend4(String extend4) {
        this.extend4 = extend4;
    }

    @Column(name = "c_json")
    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}
