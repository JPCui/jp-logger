var DailyActive = function () {
	this.id = 'daily-active',
	this.url = "/mr/daily_active.json",
	this.render = function() {
		$.ajax({
		        url: this.url,
		        wrapper: {id: this.id},
		        async: true,
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
		            var dailyActiveChart = echarts.init(document.getElementById(this.wrapper.id));

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
	}
}