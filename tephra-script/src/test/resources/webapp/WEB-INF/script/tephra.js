function tephra() {
};

tephra.BeanFactory = Java.type("org.lpw.tephra.bean.BeanFactory");
tephra.cache = tephra.BeanFactory.getBean("tephra.cache");
tephra.sql = tephra.BeanFactory.getBean("tephra.dao.sql");
tephra.ctrl = {
    header: tephra.BeanFactory.getBean("tephra.ctrl.header"),
    session: tephra.BeanFactory.getBean("tephra.ctrl.session"),
    request: tephra.BeanFactory.getBean("tephra.ctrl.request")
};
tephra.arguments = tephra.BeanFactory.getBean("tephra.script.arguments");

tephra.ready = function (func) {
    tephra.ready.functions[tephra.ready.functions.length] = func;
};

tephra.ready.functions = [];

tephra.ready.execute = function () {
    if (tephra.ready.functions.length == 0)
        return;

    for (var i = 0; i < tephra.ready.functions.length; i++) {
        if (!tephra.ready.functions[i])
            continue;

        if (typeof (tephra.ready.functions[i]) == "function")
            tephra.ready.functions[i]();
        else if (typeof (tephra.ready.functions[i]) == "string")
            eval(tephra.ready.functions[i]);

        tephra.ready.functions[i] = null;
    }
};

tephra.existsMethod = function () {
    try {
        return typeof (eval(tephra.ctrl.request.get("method"))) == "function";
    } catch (e) {
        return false;
    }
};
