package org.lpw.tephra.ctrl.validate;

import org.lpw.tephra.ctrl.TrustfulIp;
import org.lpw.tephra.ctrl.context.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * @author lpw
 */
@Controller(Validators.SIGN)
public class SignValidatorImpl extends ValidatorSupport {
    private static final String DEFAULT_FAILURE_MESSAGE_KEY = Validators.PREFIX + "illegal-sign";

    @Autowired
    protected Header header;
    @Autowired
    protected TrustfulIp trustfulIp;

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return trustfulIp.contains(header.getIp()) || request.checkSign();
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return DEFAULT_FAILURE_MESSAGE_KEY;
    }
}
