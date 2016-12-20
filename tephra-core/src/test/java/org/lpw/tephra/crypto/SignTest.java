package org.lpw.tephra.crypto;

import org.junit.Test;
import org.lpw.tephra.TestSupport;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lpw
 */
public class SignTest extends TestSupport {
    @Inject
    private Sign sign;

    @Test
    public void put() {
        sign.put(null, "sign");

        Map<String, String> map = new HashMap<>();
        sign.put(map, "sign");
    }
}
