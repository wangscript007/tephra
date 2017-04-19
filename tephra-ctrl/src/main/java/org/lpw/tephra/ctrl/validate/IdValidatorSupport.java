package org.lpw.tephra.ctrl.validate;

/**
 * @author lpw
 */
public abstract class IdValidatorSupport extends ValidatorSupport{
    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return validator.isMatchRegex("[\\da-f-]{36}", parameter) && converter.toArray(parameter, "-").length == 5;
    }
}
