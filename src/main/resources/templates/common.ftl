
<#macro common_head title>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="${staticServerPath}/css/style.css">
    <link rel="stylesheet" href="http://cdn.bootcss.com/bootstrap/3.3.4/css/bootstrap.min.css">
    <#nested>
    <style type="text/css">

        .C-cate {
            transition: all linear 0.5s;
            background-color: lightblue;
            position: relative;
            top: 0;
            left: 0;
            text-align: center;
            
            font-size: 14px;
        	height: 20px;
        	line-height: 20px;
        }
        
        .C-cate:hover {
            font-size: 16px;
        	height: 100px;
        	line-height: 100px;
        }
        
        .C-cate a {
        	display: inline-block;
        	height: 100%;
        }
        
        .C-cate a:hover {
        	background: pink;
        }

    </style>
    <title>${title}</title>
</#macro>

<#macro common_body>
<header>
  <script src="${staticServerPath}/js/canvas-nest.js" count="200" zindex="-2" opacity="0.8" color="47,135,193" type="text/javascript"></script>
</header>
<div>
    <div class="C-cate" ng-hide="A_hide">
        <a href="${serverPath}/">首页</a> | 
        <a href="${serverPath}/log/visit">VISIT</a> | 
        <a href="${serverPath}/log/info">INFO</a> | 
        <a href="${serverPath}/log/warn">WARN</a> | 
        <a href="${serverPath}/log/error">ERROR</a> | 
        <a href="${serverPath}/log/inspector">BeanInspector</a> |
        <a href="${serverPath}/mr/">MR</a>
    </div>
</div>

<#nested>
</#macro>

<#macro common_bottom>
<#nested>
</#macro>

