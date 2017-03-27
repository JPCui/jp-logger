<#include "./common.ftl">
<!DOCTYPE html>
<html lang="en">
<head>
<@common_head title="日志集中管理系统">
    <link rel="stylesheet" href="${staticServerPath}/css/style.css">
</@common_head>
</head>

<body>
<@common_body>
<div style="padding-left: 50px;">
	<h1>Log</h1>

	<div>
		<a href="${serverPath}/log/visit">VISIT</a> | 系统访问
		<hr />
		<a href="${serverPath}/log/info">INFO</a> | INFO
		<hr />
		<a href="${serverPath}/log/warn">WARN</a> | WARN
		<hr />
		<a href="${serverPath}/log/error">ERROR</a> | ERROR
		<hr />
		<a href="${serverPath}/pages/inspector">BeanInspector</a> | 调用栈
		<hr />
		<a href="${serverPath}/mr">Analysis</a> | 分析
	</div>
</div>
</@common_body>
</body>
</html>