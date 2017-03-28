<#include "../common.ftl">
<!DOCTYPE html>
<html lang="en">
<head>
<@common_head title="MR">
</@common_head>
</head>

<body>

<@common_body>
<div id="area-distribution" style="height: 400px;">
</div>

<hr/>

<div id="daily-active" style="height: 400px;">
</div>
</@common_body>


<@common_bottom>
<script src="http://cdn.bootcss.com/jquery/3.1.1/jquery.min.js"></script>
<script src="http://echarts.baidu.com/build/dist/echarts-all.js"></script>
<script src="${staticServerPath}/js/config.js"></script>
<script src="${staticServerPath}/js/ip.util.js"></script>
<script src="${staticServerPath}/js/mr/area_distribution.js"></script>
<script src="${staticServerPath}/js/mr/daily_active.js"></script>
<script type="text/javascript">
var ad = new AreaDistribution();
ad.url = "${serverPath}/mr/area_distribution.json";
ad.render();

var dailyActive = new DailyActive();
dailyActive.url = "${serverPath}/mr/daily_active.json";
dailyActive.render();
</script>
</@common_bottom>
</body>
</html>