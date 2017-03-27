<#include "../common.ftl">
<!DOCTYPE HTML>
<html>
<head>
<@common_head title="日志集中管理系统">
<style type="text/css">
table {
	font-family: verdana, arial, sans-serif;
	font-size: 11px;
	color: #333333;
	border-width: 1px;
	border-color: #666666;
	border-collapse: collapse;
	width: 96%;
	margin: 0 auto;
}

table tr:HOVER {
	background-color: silver;
}

table td {
	border-width: 1px;
	text-align: left;
	border-style: solid;
	border-color: #666666;
}

table tr.ERROR {
	color: red;
}

table tr .message {
	color: red;
}

</style>
<script type="text/javascript">
function displayStack(id) {
	var stackId = "#table-"+id+"-stack";
	console.log($(stackId).css("display"));
	if($(stackId).is(":visible")) {
		$(stackId).css("display", "none");
	} else {
		$(stackId).css("display", "");
	}
	
}
</script>
</@common_head>
</head>

<body>
<@common_body>
<h1 style="text-align: center;">LOG</h1>
<div>
<table>
<tr>
<td style="width :50%;">
	<#if prevPage != _pageNum>
	<a href="?time=${time}&_pageNum=${prevPage }&keyword=${keyword}">上一页</a>
	</#if>
	<#if prevPage == _pageNum>&nbsp;</#if>
</td>
<td>
	<#if nextPage != _pageNum>
	<a href="?time=${time}&_pageNum=${nextPage }&keyword=${keyword}">下一页</a>
	</#if>
	<#if nextPage == _pageNum>&nbsp;</#if>
</td>
</tr>
</table>
<#list list as l>
<table id="table-${l.id}">
		<tr class="${l.level}">
			<td style="width: 100px;" onclick="javascript:displayStack('${l.id}');">
			<a href="#table-${l.id}">[${l.level}]</a>
			</td>
			<td>
			${l.time?string("yyyy-MM-dd HH:mm:ss(SSS)")}
			${l.clazz}.${l.method}
			(${l.line})
				-> ${l.thread} ${l.message}
			</td>
			<td style="width: 100px;" title="${l.server.hostName}">${l.server.hostAddress}</td>
		</tr>
</table>
<table id="table-${l.id}-stack" style="display: none;">
		<#list l.throwables as th>
		<tr>
			<td style="width: 100px;">&nbsp;</td>
			<td class="message">${th.clazz}: ${stringIgnoreNull(th.message)}</td>
		</tr>
		<#list th.stackTraces as st>
		<tr>
			<td style="width: 100px;">&nbsp;</td>
			<td>at: ${st.className}.${st.method }(${st.line})</td>
		</tr>
		</#list>
		</#list>
</table>
</#list>
</div>
</@common_body>

<@common_bottom>
</@common_bottom>
</body>
</html>