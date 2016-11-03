package org.lpw.tephra.ctrl;

import org.lpw.tephra.scheduler.SecondsJob;
import org.lpw.tephra.util.Context;
import org.lpw.tephra.util.Converter;
import org.lpw.tephra.util.Io;
import org.lpw.tephra.util.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * @auth lpw
 */
@Controller("tephra.ctrl.trustful-ip")
public class TrustfulIpImpl implements TrustfulIp, SecondsJob {
    @Autowired
    protected Context context;
    @Autowired
    protected Converter converter;
    @Autowired
    protected Validator validator;
    @Autowired
    protected Io io;
    @Value("${tephra.ctrl.trustful-ip:/WEB-INF/trustful-ip}")
    protected String trustfulIp;
    protected Set<String> ips = new HashSet<>();
    protected Set<String> patterns = new HashSet<>();
    protected long lastModified = 0L;

    @Override
    public boolean contains(String ip) {
        if (ips.contains(ip))
            return true;

        for (String pattern : patterns)
            if (validator.isMatchRegex(pattern, ip))
                return true;

        return false;
    }

    @Override
    public void executeSecondsJob() {
        File file = new File(context.getAbsolutePath(trustfulIp));
        if (file.lastModified()<=lastModified)
            return;

        lastModified = file.lastModified();
        Set<String> ips = new HashSet<>();
        Set<String> patterns = new HashSet<>();
        for (String string : converter.toArray(new String(io.read(file.getAbsolutePath())), "\n")) {
            string = string.trim();
            if (string.equals("") || string.startsWith("#"))
                continue;

            if (string.startsWith("rg"))
                patterns.add(string.substring(2));
            else
                ips.add(string);
        }
        this.ips = ips;
        this.patterns = patterns;
    }
}
