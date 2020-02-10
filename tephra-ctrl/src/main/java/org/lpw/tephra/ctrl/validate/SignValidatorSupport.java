package org.lpw.tephra.ctrl.validate;

import org.lpw.tephra.crypto.Sign;
import org.lpw.tephra.ctrl.context.Header;
import org.lpw.tephra.ctrl.context.Request;
import org.lpw.tephra.ctrl.security.TrustfulIp;
import org.lpw.tephra.util.Logger;

import javax.inject.Inject;

/**
 * @author lpw
 */
public class SignValidatorSupport extends ValidatorSupport implements SignValidator {
    @Inject
    private Sign sign;
    @Inject
    private Logger logger;
    @Inject
    private Header header;
    @Inject
    private TrustfulIp trustfulIp;
    private ThreadLocal<Boolean> threadLocal = new ThreadLocal<>();

    @Override
    public void setSignEnable(boolean enable) {
        threadLocal.set(enable);
    }

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        if (!enable() || trustfulIp.contains(header.getIp())
                || sign.verify(request.getMap(), validator.isEmpty(validate.getString()) ? null : validate.getString()[0]))
            return true;

        logger.warn(null, "参数[{}]签名验证不通过！", converter.toString(request.getMap()));

        return false;
    }

    private boolean enable() {
        return threadLocal.get() == null || threadLocal.get();
    }

    @Override
    public int getFailureCode(ValidateWrapper validate) {
        return 999995;
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return Validators.PREFIX + "illegal-sign";
    }
}
