package org.lpw.tephra.ctrl.validate;

import org.springframework.stereotype.Controller;

/**
 * @author lpw
 */
@Controller(Validators.IN)
public class InValidatorImpl extends ValidatorSupport {
    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        if (validator.isEmpty(validate.getNumber())) {
            for (String string : validate.getString())
                if (string.equals(parameter))
                    return true;

            return false;
        }

        int p = numeric.toInt(parameter);
        for (int n : validate.getNumber())
            if (p == n)
                return true;

        return false;
    }

    @Override
    protected Object[] getFailureMessageArgs(ValidateWrapper validate) {
        Object[] args = super.getFailureMessageArgs(validate);

        return new Object[]{args[0],
                converter.toString(validator.isEmpty(validate.getNumber()) ? validate.getString() : validate.getNumber())};
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return Validators.PREFIX + "not-in";
    }
}
