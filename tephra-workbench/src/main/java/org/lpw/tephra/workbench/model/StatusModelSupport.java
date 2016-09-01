package org.lpw.tephra.workbench.model;

import org.lpw.tephra.dao.model.ModelSupport;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * @author lpw
 */
@MappedSuperclass()
public class StatusModelSupport extends ModelSupport implements StatusModel {
    private int status;

    @Column(name = "c_status")
    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public void setStatus(int status) {
        this.status = status;
    }
}
