function tephra() {
};

tephra.ajax = function (uri, params) {
    return $.ajax({
        cache: false,
        async: false,
        type: "POST",
        dataType: "json",
        url: uri,
        data: params
    }).responseJSON;
};

tephra.ajax.async = function (uri, params, callback) {
    $.ajax({
        cache: false,
        async: true,
        type: "POST",
        dataType: "json",
        url: uri,
        data: params,
        success: callback(response)
    });
};
