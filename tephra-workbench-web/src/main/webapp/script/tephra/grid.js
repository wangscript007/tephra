$.fn.datagrid.defaults.border = false;
$.fn.datagrid.defaults.idField = "id";
$.fn.datagrid.defaults.fit = true;
$.fn.datagrid.defaults.remoteSort = false;
$.fn.datagrid.defaults.nowrap = false;
$.fn.datagrid.defaults.singleSelect = true;
$.fn.datagrid.defaults.rownumbers = true;
$.fn.datagrid.defaults.pagination = true;
$.fn.datagrid.defaults.striped = true;
$.fn.datagrid.defaults.pageList = [10, 20, 40, 80];
$.fn.datagrid.defaults.pageSize = 20;
$.fn.pagination.defaults.layout = ["list", "sep", "first", "prev", "sep", "links", "sep", "next", "last", "sep", "refresh"];

tephra.grid = function (uri, actions, properties) {
    $("#workbench").html("<table id='workbench-grid'></table>");
    tephra.grid.convert(uri, actions, properties);
    $("#workbench-grid").datagrid(tephra.grid.metadata);
    $("#workbench-grid").datagrid("getPager").pagination({
        onSelectPage: function (pageNumber, pageSize) {
            tephra.grid.load({
                pageSize: pageSize,
                pageNumber: pageNumber
            });
        }
    });
    tephra.grid.load();
};

tephra.grid.metadata = null;

tephra.grid.convert = function (uri, actions, properties) {
    tephra.grid.metadata = {
        uri: uri,
        border: false,
        toolbar: actions
    };
    if (tephra.grid.metadata.toolbar.length > 0)
        tephra.grid.metadata.toolbar[tephra.grid.metadata.toolbar.length] = "-";
    tephra.grid.metadata.toolbar[tephra.grid.metadata.toolbar.length] = {
        iconCls: "icon-search",
        text: tephra.workbench.message("tephra.workbench.search"),
        handler: tephra.grid.search
    };

    var searches = [];
    var columns = [];
    var frozenColumns = [];
    for (var i = 0; i < properties.length; i++) {
        var column = {
            field: properties[i].name,
            title: properties[i].label,
            sortable: true
        };
        searches[searches.length] = column;
        if (column.frozen)
            frozenColumns[frozenColumns.length] = column;
        else
            columns[columns.length] = column;
    }

    tephra.grid.metadata.searches = searches;
    tephra.grid.metadata.columns = [columns];
    tephra.grid.metadata.frozenColumns = [frozenColumns];
};

tephra.grid.load = function (params) {
    if (!params)
        params = {};
    if (!params.pageSize) {
        var options = $("#workbench-grid").datagrid("getPager").pagination("options");
        params.pageSize = options.pageSize;
        params.pageNumber = options.pageNumber;
    }

    var data = tephra.workbench.get(tephra.grid.metadata.uri, params);
    if (!data)
        return;

    $("#workbench-grid").datagrid("unselectAll");
    tephra.grid.select.index = -1;
    tephra.grid.select.row = null;
    $("#workbench-grid").datagrid({data: data.list});
    $("#workbench-grid").datagrid("getPager").pagination("refresh", {
        total: data.count,
        pageSize: data.size,
        pageNumber: data.number
    });
};

tephra.grid.search = function () {
};

tephra.grid.search.ok = function () {
};

tephra.grid.update = function (uri, row) {
    $("#workbench-grid").datagrid("updateRow", {
        index: $("#workbench-grid").datagrid("getRowIndex", row),
        row: tephra.workbench.get(uri, {id: row.id})
    });
};

tephra.grid.select = function (type) {
    var row = $("#workbench-grid").datagrid("getSelected");
    if (!row)
        tephra.workbench.notice(8901, tephra.workbench.message("tephra.workbench.select.empty").replace("{0}", tephra.workbench.message(type)));

    return row;
};

tephra.grid.select.id = function (type) {
    var row = tephra.grid.select(type);

    return row ? row.id : null;
};
