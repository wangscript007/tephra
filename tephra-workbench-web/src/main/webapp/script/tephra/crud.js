tephra.crud = function () {
    var actions = [{
        iconCls: "icon-add",
        text: tephra.workbench.message("tephra.workbench.create"),
        handler: tephra.crud.create
    }, {
        iconCls: "icon-edit",
        text: tephra.workbench.message("tephra.workbench.modify"),
        handler: tephra.crud.modify
    }, {
        iconCls: "icon-remove",
        text: tephra.workbench.message("tephra.workbench.delete"),
        handler: tephra.crud.remove
    }];

    tephra.grid(tephra.workbench.metadata.uri + "query", actions, tephra.workbench.metadata.properties);
};

tephra.crud.create = function () {
    tephra.workbench.confirm("add", "tephra.workbench.create.title", "<table id='workbench-propertygrid'></table>", tephra.crud.create.ok);
    tephra.propertygrid(tephra.workbench.get(tephra.workbench.metadata.uri + "create"));
};

tephra.crud.create.ok = function () {
    return tephra.crud.save({});
};

tephra.crud.modify = function () {
    var row = tephra.grid.select("tephra.workbench.modify");
    if (!row)
        return;

    tephra.grid.update(tephra.workbench.metadata.uri + "modify", row);
    tephra.workbench.confirm("edit", "tephra.workbench.modify.title", "<table id='workbench-propertygrid'></table>", tephra.crud.modify.ok);
    tephra.propertygrid(row);
};

tephra.crud.modify.ok = function () {
    return tephra.crud.save(tephra.grid.select("tephra.workbench.modify"));
};

tephra.crud.save = function (row) {
    var rows = tephra.propertygrid.get();
    for (var i = 0; i < tephra.workbench.metadata.properties.length; i++) {
        for (var j = 0; j < rows.length; j++) {
            if (tephra.workbench.metadata.properties[i].label == rows[j].name) {
                row[tephra.workbench.metadata.properties[i].name] = rows[j].value;

                break;
            }
        }
    }
    if (tephra.workbench.get(tephra.workbench.metadata.uri + "save", row) == null)
        return "keep";

    tephra.grid.load();
};

tephra.crud.remove = function () {
    var row = tephra.grid.select("tephra.workbench.delete");
    if (!row)
        return;

    tephra.workbench.confirm("remove", "tephra.workbench.delete.confirm", "<table id='workbench-propertygrid'></table>", tephra.crud.remove.ok);
    tephra.propertygrid(row);
};

tephra.crud.remove.ok = function () {
    tephra.workbench.get(tephra.workbench.metadata.uri + "delete", {id: tephra.grid.select.id("tephra.workbench.delete")});
    tephra.grid.load();
};
