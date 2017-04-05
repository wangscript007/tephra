package org.lpw.tephra.ctrl.validate;

import org.springframework.stereotype.Controller;

/**
 * @author lpw
 */
@Controller(Validators.LONGITUDE)
public class LongitudeValidatorImpl extends ValidatorSupport {
    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return validator.isMatchRegex("^\\d{1,3}\\.\\d+$", parameter) && Math.abs(converter.toDouble(parameter) - 90) <= 90;
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return Validators.PREFIX + "illegal-latitude";
    }
}
