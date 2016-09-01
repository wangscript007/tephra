package org.lpw.tephra.workbench.model;

/**
 * @author lpw
 */
public interface DomainModel extends StatusModel {
    /**
     * 获取域ID值。
     *
     * @return 域ID值。
     */
    String getDomain();

    /**
     * 设置域ID值。
     *
     * @param domain 域ID值。
     */
    void setDomain(String domain);
}
