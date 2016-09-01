package org.lpw.tephra.ctrl.validate;

import org.springframework.stereotype.Controller;

/**
 * @author lpw
 */
@Controller(Validators.MAX_LENGTH)
public class MaxLengthValidatorImpl extends ValidatorSupport {
    private static final String DEFAULT_FAILURE_MESSAGE_KEY = Validators.PREFIX + "over-max-length";

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return validator.isEmpty(parameter) || parameter.length() <= validate.getNumber()[0];
    }

    @Override
    protected Object[] getFailureMessageArgs(ValidateWrapper validate) {
        if (validator.isEmpty(validate.getFailureArgKeys()))
            return new Object[]{validate.getNumber()[0]};

        return new Object[]{message.get(validate.getFailureArgKeys()[0]), validate.getNumber()[0]};
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return DEFAULT_FAILURE_MESSAGE_KEY;
    }
}
