package org.lpw.tephra.ctrl.validate;

import org.lpw.tephra.crypto.Sign;
import org.lpw.tephra.ctrl.context.Header;
import org.lpw.tephra.ctrl.security.TrustfulIp;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

/**
 * @author lpw
 */
@Controller(Validators.SIGN)
public class SignValidatorImpl extends ValidatorSupport implements SignValidator {
    @Inject
    private Sign sign;
    @Inject
    private Header header;
    @Inject
    private TrustfulIp trustfulIp;
    @Value("${tephra.ctrl.validate.sign:tephra-ctrl-sign}")
    private String signKey;
    private ThreadLocal<Boolean> threadLocal = new ThreadLocal<>();

    @Override
    public void setSignEnable(boolean enable) {
        threadLocal.set(enable);
    }

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        if (!enable() || trustfulIp.contains(header.getIp()))
            return true;

        String signKey = request.get(this.signKey);
        if (validator.isEmpty(signKey))
            signKey = "";

        return signKey.equals(parameter) && sign.verify(request.getMap(), signKey);
    }

    private boolean enable() {
        return threadLocal.get() == null || threadLocal.get();
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
