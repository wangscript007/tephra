$.fn.propertygrid.defaults.border = false;

tephra.propertygrid = function (row) {
    var rows = [];
    for (var i = 0; i < tephra.workbench.metadata.properties.length; i++) {
        var object = {
            name: tephra.workbench.metadata.properties[i].label,
            value: row ? row[tephra.workbench.metadata.properties[i].name] : ""
        };
        if (tephra.workbench.metadata.properties[i].editable)
            object.editor = tephra.workbench.metadata.properties[i].type + "box";
        rows[rows.length] = object;
    }

    $("#workbench-propertygrid").propertygrid({
        data: rows
    });
};

tephra.propertygrid.get = function () {
    return $("#workbench-propertygrid").propertygrid("getChanges");
};
