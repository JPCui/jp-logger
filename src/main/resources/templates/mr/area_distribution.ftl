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
</body>
</html>