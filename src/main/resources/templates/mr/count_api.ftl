<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
  <link rel="stylesheet" href="${staticServerPath}/css/style.css" />
  <title>API统计</title>
  
<style>
    * {
        list-style-type: none;
    }
    div.box {
        height: 500px;
        overflow-y: scroll;
    }
    ul {
        display: table;
    }
    ul li {
        display: table-row;
        border-bottom: 1px solid silver;
    }
    ul li c {
        display: table-cell;
        padding: 10px 15px;
    }
</style>
  
</head>
<body id="body">

	
<h1>API统计</h1>
<div class="box">
    <ul>
    	<#list datas?keys as key>
        <li><c>${key}</c><c>${datas[key]}</c></li>
        </#list>
    </ul>
</div>


</body>
</html>
