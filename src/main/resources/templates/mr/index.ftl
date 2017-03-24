<#include "../common.ftl">
<!DOCTYPE html>
<html lang="en">
<head>
<@common_head title="MR">
    <link rel="stylesheet" href="${staticServerPath}/css/style.css">
    <style>
        #daily-active {
            height: 400px;
        }
        #area-distribution {
            height: 400px;
        }
    </style>
</@common_head>
</head>

<body>

<@common_body>
<div id="area-distribution">
</div>

<hr/>

<div id="daily-active">
</div>
</@common_body>


<@common_bottom>
<script src="http://cdn.bootcss.com/jquery/3.1.1/jquery.min.js"></script>
<script src="http://echarts.baidu.com/build/dist/echarts-all.js"></script>
<script src="${staticServerPath}/js/config.js"></script>
<script src="${staticServerPath}/js/ip.util.js"></script>
<script type="text/javascript">
    $.ajax({
        url: "${serverPath}/mr/count_ip.json",
        async: false,
        success: function (rs) {
            var tempData = new Object();

			var datas = rs.datas;
			for(var ip in datas) {
                var value = datas[ip];

                var city = IPUtil.getCityByIp(ip);
                var province = city.province;
                if (!tempData[province]) {
                    tempData[province] = 0;
                }
                tempData[province] += value;
            }

            var data = new Array();
            for (var province in tempData) {
                data.push({name: province, value: tempData[province]});
            }


            // 基于准备好的dom，初始化echarts图表
            var dailyActiveChart = echarts.init(document.getElementById('area-distribution'));

            var option = {
                title: {
                    text: '访问量',
                    subtext: '基于访问日志',
                    x: 'center'
                },
                tooltip: {
                    trigger: 'item'
                },
                legend: {
                    orient: 'vertical',
                    x: 'left',
                    data: ['全部数据']
                },
                dataRange: {
                    x: 'left',
                    y: 'bottom',
                    splitList: [
                        {start: 1500},
                        {start: 900, end: 1500},
                        {start: 310, end: 1000},
                        {start: 200, end: 300},
                        {start: 10, end: 200, label: '10 到 200（自定义label）'},
                        {start: 5, end: 5, label: '5（自定义特殊颜色）', color: 'black'},
                        {end: 10}
                    ],
                    color: ['#E0022B', '#E09107', '#A3E00B']
                },
                toolbox: {
                    show: true,
                    orient: 'vertical',
                    x: 'right',
                    y: 'center',
                    feature: {
                        mark: {show: true},
                        dataView: {show: true, readOnly: false},
                        restore: {show: true},
                        saveAsImage: {show: true}
                    }
                },
                roamController: {
                    show: true,
                    x: 'right',
                    mapTypeControl: {
                        'china': true
                    }
                },
                series: [
                    {
                        name: '订单量',
                        type: 'map',
                        mapType: 'china',
                        roam: false,
                        itemStyle: {
                            normal: {
                                label: {
                                    show: true,
                                    textStyle: {
                                        color: "rgb(249, 249, 249)"
                                    }
                                }
                            },
                            emphasis: {label: {show: true}}
                        },
                        data: data
                    }
                ]
            };

            console.log(option);

            // 为echarts对象加载数据
            dailyActiveChart.setOption(option);

        }
    });

</script>
<script type="text/javascript">
    $.ajax({
        url: "${serverPath}/mr/daily_active.json",
        async: false,
        success: function (datas) {
            var legends = new Array();
            var data = new Array();

            // 填充data
            $(datas).each(function (i, ds) {
                legends.push(ds.date);

                data[i] = new Array();
                $(ds.datas).each(function (j, d) {
                    data[i][d.date] = d.value;
                });
            });

            // 补充0
            $(data).each(function (i, ds) {
                for (var i = 0; i <= 23; i++) {
                    if (!ds[i]) {
                        ds[i] = 0;
                    }
                }
            });

            // 创建series
            var series = new Array();
            $(data).each(function (i, ds) {
                series.push({
                    "name": datas[i].date,
                    "type": "line",
                    "data": ds
                });
            });

            // 基于准备好的dom，初始化echarts图表
            var dailyActiveChart = echarts.init(document.getElementById('daily-active'));

            var option = {
                title: {
                    text: "过去三天的访问统计",
                    subtext: "24小时分布"
                },
                tooltip : {
                    trigger: 'axis',
                    show: true
                },
                legend: {
                    data: legends
                },
                xAxis: [
                    {
                        type: 'category',
                        boundaryGap: false,
                        data: ["0:00", "1:00", "2:00", "3:00", "4:00", "5:00", "6:00", "7:00", "8:00", "9:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00"]
                    }
                ],
                yAxis: [
                    {
                        type: 'value'
                    }
                ],
                series: series
            };

            console.log(option);

            // 为echarts对象加载数据
            dailyActiveChart.setOption(option);

        }
    });

</script>
</@common_bottom>
</body>
</html>