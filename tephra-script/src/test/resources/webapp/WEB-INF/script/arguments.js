function arguments_execute() {
    var arg = tephra.arguments.get("arg");
    tephra.arguments.set("arg", "arg from javascript");

    return arg;
};

function arguments_all() {
    var array = tephra.arguments.all();
    var sum = 0;
    for (var i = 0; i < 10; i++)
        sum += array["arg" + i];

    return sum;
};