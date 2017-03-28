<!DOCTYPE html>
<html>
<head><title></title>
    <link rel="stylesheet" href="${staticServerPath}/css/style.css">
    <style>
        #area-distribution {
            height: 400px;
        }
    </style>
</head>
<body>
<#include "../common.ftl">

<div id="area-distribution">

</div>
<script src="http://cdn.bootcss.com/jquery/3.1.1/jquery.min.js"></script>
<script src="http://echarts.baidu.com/build/dist/echarts-all.js"></script>
<script src="${staticServerPath}/js/config.js"></script>
<script src="${staticServerPath}/js/ip.util.js"></script>
<script src="${staticServerPath}/js/mr/area_distribution.js"></script>
<script type="text/javascript">
var ad = new AreaDistribution();
ad.url = "${serverPath}/mr/area_distribution.json";
ad.render();
</script>
</body>
</html>