package org.lpw.tephra.test.mock;

/**
 * @author lpw
 */
public class MockSessionImpl implements MockSession {
    private String id;

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }
}
