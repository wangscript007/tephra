package org.lpw.tephra.wormhole;

/**
 * @author lpw
 */
public enum Protocol {
    Http("http://"),
    Https("https://"),
    Ws("ws://"),
    Wss("wss://");

    private String prefix;

    Protocol(String prefix) {
        this.prefix = prefix;
    }

    public String get() {
        return prefix;
    }
}
