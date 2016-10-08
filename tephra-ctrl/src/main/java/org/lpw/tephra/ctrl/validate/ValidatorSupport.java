package org.lpw.tephra.ctrl.validate;

import org.lpw.tephra.ctrl.context.Request;
import org.lpw.tephra.ctrl.execute.ExecutorHelper;
import org.lpw.tephra.util.Converter;
import org.lpw.tephra.util.Message;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author lpw
 */
public abstract class ValidatorSupport implements Validator {
    @Autowired
    protected org.lpw.tephra.util.Validator validator;
    @Autowired
    protected Converter converter;
    @Autowired
    protected Message message;
    @Autowired
    protected Request request;
    @Autowired
    protected ExecutorHelper executorHelper;

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return false;
    }

    @Override
    public boolean validate(ValidateWrapper validate, String[] parameters) {
        return false;
    }

    @Override
    public int getFailureCode(ValidateWrapper validate) {
        return 0;
    }

    @Override
    public String getFailureMessage(ValidateWrapper validate) {
        return message.get(validator.isEmpty(validate.getFailureKey()) ? getDefaultFailureMessageKey() : validate.getFailureKey(),
                getFailureMessageArgs(validate));
    }

    protected Object[] getFailureMessageArgs(ValidateWrapper validate) {
        if (validator.isEmpty(validate.getFailureArgKeys()))
            return new Object[]{message.get(executorHelper.get().getKey() + "." + validate.getParameter())};

        Object[] args = new Object[validate.getFailureArgKeys().length];
        for (int i = 0; i < args.length; i++)
            args[i] = message.get(validate.getFailureArgKeys()[i]);

        return args;
    }

    protected abstract String getDefaultFailureMessageKey();
}
