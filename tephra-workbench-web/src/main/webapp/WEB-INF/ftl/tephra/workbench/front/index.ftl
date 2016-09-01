<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title></title>
    <link rel="stylesheet" type="text/css" href="theme/easyui/metro/easyui.css"/>
    <link rel="stylesheet" type="text/css" href="theme/easyui/icon.css"/>
    <link rel="stylesheet" type="text/css" href="theme/easyui/color.css"/>
    <link rel="stylesheet" type="text/css" href="theme/tephra/default.css"/>
    <script type="text/javascript" src="script/jquery/jquery-2.1.4.min.js"></script>
    <script type="text/javascript" src="script/jquery/jquery.easyui-1.4.4.min.js"></script>
    <script type="text/javascript" src="script/jquery/easyui-lang-zh_CN.js"></script>
    <script type="text/javascript" src="script/tephra/util.js"></script>
    <script type="text/javascript" src="script/tephra/grid.js"></script>
    <script type="text/javascript" src="script/tephra/propertygrid.js"></script>
    <script type="text/javascript" src="script/tephra/workbench.js"></script>
    <script type="text/javascript" src="script/tephra/crud.js"></script>
</head>
<body class="easyui-layout">
<div data-options="region:'north'" style="height:50px">
    <div class="workbench-notice" style="display: none;"></div>
</div>
<div data-options="region:'south'" style="height:50px;"></div>
<div data-options="region:'west',title:' '" style="width:20%;">
    <div data-options="border:false" class="easyui-accordion" style="width:100%;height:100%;">
    <#list data.menus as menu>
        <div title="${menu.label}">
            <#list menu.children as child>
                <a class="menu-item" href="javascript:void(0);" onclick="javascript:tephra.workbench.menu('${menu.label}','${child.label}','${child.uri}');">${child.label}</a>
            </#list>
        </div>
    </#list>
    </div>
</div>
<div id="workbench" data-options="region:'center',title:' '">
</div>
<div id="workbench-confirm" class="easyui-dialog" style="width: 50%;height: 50%;" data-options="closed:true"></div>
<script type="text/javascript">
</script>
</body>
</html>
