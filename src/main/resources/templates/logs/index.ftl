<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

<title>日志集中管理系统</title>
<style type="text/css">

    .half-circle {
        width: 30px;
        height: 30px;
        /* 水平半径 = width/2, 垂直半径 = height + padding */
        border-radius: 0 0 20px 20px/0 0 20px 20px;
        background-color: #f29900;
        color: #fff;
        text-align: center;
        font-size: 1.6rem;
    }

    .C-cate {
        margin: 10px auto;
        width: 90%;
        transition: all linear 0.5s;
        background-color: lightblue;
        position: relative;
        top: 0;
        left: 0;
    }

    .C-tip {
        position: absolute;
        right: 10px;
        top: -5px;
    }

    .C-cate.ng-hide {
        background-color: greenyellow;
        top:-200px;
        left: 200px;
    }
</style>

<style type="text/css">
body {
	text-align: center;
	margin: 0 auto;
}

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

a {
	color: black;
}

</style>
<script type="text/javascript" 
	src="${staticServerPath}/lib/jquery/jquery-1.11.1.js"></script>
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
</head>

<body>
<#include "../common.ftl">
<h1>LOG</h1>
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

</body>
</html>