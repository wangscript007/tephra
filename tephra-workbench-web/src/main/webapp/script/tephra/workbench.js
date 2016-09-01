tephra.workbench = function () {
};

tephra.workbench.menu = function (parent, label, uri) {
    $(".layout-panel-center .panel-title").html(parent + " >> " + label);
    tephra.workbench.metadata = tephra.ajax(uri + "metadata").data;
    tephra.crud();
};

tephra.workbench.metadata = null;

tephra.workbench.get = function (uri, params) {
    var json = tephra.ajax(uri, params);
    if (json.message && json.message.trim().length > 0)
        tephra.workbench.notice(json.code, json.message);

    return json.code == 0 ? json.data : null;
};

tephra.workbench.notice = function (code, message) {
    var notice = $(".workbench-notice");
    notice.html(message);
    notice.attr("class", "workbench-notice workbench-" + (code == 0 ? "success" : "failure") + "-notice");
    notice.fadeIn(1000);
    setTimeout(tephra.workbench.notice.hide, (code == 0 ? 10 : 30) * 1000);
};

tephra.workbench.notice.hide = function () {
    $(".workbench-notice").fadeOut(1000);
};

tephra.workbench.messages = [];

tephra.workbench.message = function (key) {
    if (!tephra.workbench.messages[key]) {
        var message = tephra.workbench.get("message", {key: key});
        if (!message)
            return null;

        tephra.workbench.messages[key] = message;
    }

    return tephra.workbench.messages[key];
};

tephra.workbench.confirm = function (icon, title, content, ok, cancel) {
    $("#workbench-confirm").html(content);
    $("#workbench-confirm").dialog({
        iconCls: "icon-" + (icon ? icon : "blank"),
        title: tephra.workbench.message(title),
        modal: true,
        buttons: [{
            iconCls: "icon-ok",
            text: tephra.workbench.message("tephra.workbench.ok"),
            handler: function () {
                var result = null;
                if (ok)
                    result = ok();
                if (result == "keep")
                    return;

                $("#workbench-confirm").dialog("close");
            }
        }, {
            iconCls: "icon-cancel",
            text: tephra.workbench.message("tephra.workbench.cancel"),
            handler: function () {
                var result = null;
                if (cancel)
                    result = cancel();
                if (result == "keep")
                    return;

                $("#workbench-confirm").dialog("close");
            }
        }]
    });
    $("#workbench-confirm").dialog("open");
};
