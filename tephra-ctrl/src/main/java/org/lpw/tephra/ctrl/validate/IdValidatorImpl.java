package org.lpw.tephra.ctrl.validate;

import org.springframework.stereotype.Controller;

/**
 * @author lpw
 */
@Controller(Validators.ID)
public class IdValidatorImpl extends IdValidatorSupport {
    @Override
    protected String getDefaultFailureMessageKey() {
        return Validators.PREFIX + "illegal-id";
    }
}
