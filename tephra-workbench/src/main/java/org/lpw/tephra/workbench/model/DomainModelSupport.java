package org.lpw.tephra.workbench.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * @author lpw
 */
@MappedSuperclass()
public class DomainModelSupport extends StatusModelSupport implements DomainModel {
    private String domain;

    @Column(name = "c_domain_id")
    @Override
    public String getDomain() {
        return domain;
    }

    @Override
    public void setDomain(String domain) {
        this.domain = domain;
    }
}
