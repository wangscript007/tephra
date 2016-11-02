package org.lpw.tephra.ctrl;

import org.lpw.tephra.scheduler.MinuteJob;
import org.lpw.tephra.util.Context;
import org.lpw.tephra.util.Converter;
import org.lpw.tephra.util.Io;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @auth lpw
 */
@Controller("tephra.ctrl.trustful-ip")
public class TrustfulIpImpl implements TrustfulIp, MinuteJob {
    @Autowired
    protected Context context;
    @Autowired
    protected Converter converter;
    @Autowired
    protected Io io;
    @Value("${tephra.ctrl.trustful-ip:/WEB-INF/trustful-ip}")
    protected String trustfulIp;
    protected Set<String> ips = new HashSet<>();
    protected Set<Pattern> patterns = new HashSet<>();
    protected long lastModified = 0L;

    @Override
    public boolean contains(String ip) {
        if (ips.contains(ip))
            return true;

        for (Pattern pattern : patterns)
            if (pattern.matcher(ip).matches())
                return true;

        return false;
    }

    @Override
    public void executeMinuteJob() {
        File file = new File(context.getAbsolutePath(trustfulIp));
        if (lastModified >= file.lastModified())
            return;

        lastModified = file.lastModified();
        Set<String> ips = new HashSet<>();
        Set<Pattern> patterns = new HashSet<>();
        for (String string : converter.toArray(new String(io.read(file.getAbsolutePath())), "\n")) {
            string = string.trim();
            if (string.equals("") || string.startsWith("#"))
                continue;

            if (string.startsWith("rg"))
                patterns.add(Pattern.compile(string.substring(2)));
            else
                ips.add(string);
        }
        this.ips = ips;
        this.patterns = patterns;
    }
}
