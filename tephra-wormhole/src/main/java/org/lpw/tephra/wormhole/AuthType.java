package org.lpw.tephra.wormhole;

public enum AuthType {
    /**
     * 生产者。
     */
    Producer("producer"),
    /**
     * 消费者。
     */
    Consumer("consumer");

    private String name;

    AuthType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
