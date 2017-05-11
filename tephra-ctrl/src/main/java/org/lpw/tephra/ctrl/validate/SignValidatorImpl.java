package org.lpw.tephra.ctrl.validate;

import org.lpw.tephra.ctrl.TrustfulIp;
import org.lpw.tephra.ctrl.context.Header;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

/**
 * @author lpw
 */
@Controller(Validators.SIGN)
public class SignValidatorImpl extends ValidatorSupport {
    @Inject
    private Header header;
    @Inject
    private TrustfulIp trustfulIp;
    @Value("${" + Validators.SIGN + ".enable:true}")
    private boolean enable;

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return !enable || trustfulIp.contains(header.getIp()) || request.checkSign();
    }

    @Override
    public int getFailureCode(ValidateWrapper validate) {
        return 9995;
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return Validators.PREFIX + "illegal-sign";
    }
}
