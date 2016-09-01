package org.lpw.tephra.workbench.domain;

/**
 * @author lpw
 */
public interface DomainDao {
    DomainModel findByKey(String key);
}
