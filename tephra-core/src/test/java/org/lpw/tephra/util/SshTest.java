package org.lpw.tephra.util;

import org.junit.Test;
import org.lpw.tephra.test.CoreTestSupport;

import javax.inject.Inject;

/**
 * @author lpw
 */
public class SshTest extends CoreTestSupport {
    @Inject
    private Ssh ssh;

    @Test
    public void shell() {
        System.out.println(ssh.shell("192.168.1.77", 22, "root", "root", "pwd", "ll"));
    }
}
