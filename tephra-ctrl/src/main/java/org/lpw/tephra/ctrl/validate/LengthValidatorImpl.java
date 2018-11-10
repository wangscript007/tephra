package org.lpw.tephra.ctrl.validate;

import org.springframework.stereotype.Controller;

/**
 * @author lpw
 */
@Controller(Validators.LENGTH)
public class LengthValidatorImpl extends ValidatorSupport {
    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        if (validator.isEmpty(parameter))
            return validate.getNumber()[0] == 0;

        return parameter.length() == validate.getNumber()[0];
    }

    @Override
    protected Object[] getFailureMessageArgs(ValidateWrapper validate) {
        Object[] args = super.getFailureMessageArgs(validate);

        return new Object[]{args[0], validate.getNumber()[0]};
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return Validators.PREFIX + "illegal-length";
    }
}
