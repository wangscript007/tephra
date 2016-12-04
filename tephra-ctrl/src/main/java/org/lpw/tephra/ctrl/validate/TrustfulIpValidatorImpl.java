package org.lpw.tephra.ctrl.validate;

import org.lpw.tephra.ctrl.TrustfulIp;
import org.lpw.tephra.ctrl.context.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * @author lpw
 */
@Controller(Validators.TRUSTFUL_IP)
public class TrustfulIpValidatorImpl extends ValidatorSupport {
    @Autowired
    protected Header header;
    @Autowired
    protected TrustfulIp trustfulIp;

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return trustfulIp.contains(header.getIp());
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return Validators.PREFIX + "distrust-ip";
    }
}
