package org.lpw.tephra.workbench.crud;

import org.lpw.tephra.ctrl.validate.ValidateWrapper;
import org.lpw.tephra.ctrl.validate.ValidatorSupport;
import org.springframework.stereotype.Controller;

/**
 * @author lpw
 */
@Controller(CrudService.SELECT_VALIDATOR)
public class SelectValidatorImpl extends ValidatorSupport {
    @Override
    protected String getDefaultFailureMessageKey() {
        return "tephra.workbench.select.empty";
    }

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return !validator.isEmpty(parameter);
    }
}
