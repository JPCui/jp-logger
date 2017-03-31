/**
 * 用户请求地域分布
 */
var AreaDistribution = function() {
	// URI : /mr/area_distribution.json
	this.url = null, this.id = "area-distribution", this.render = function() {
		$.ajax({
			url : this.url,
			// 用于传递到ajax.success里面的参数
			wrapper : {
				id : this.id
			},
			async : true,
			success : function(rs) {
				var tempData = rs.datas;

				var data = new Array();
				for ( var province in tempData) {
					data.push({
						name : province,
						value : tempData[province]
					});
				}

				// 基于准备好的dom，初始化echarts图表
				var dailyActiveChart = echarts.init(document
						.getElementById(this.wrapper.id));

				var option = {
					title : {
						text : '访问地域分布图',
						subtext : '基于访问日志',
						x : 'center'
					},
					tooltip : {
						trigger : 'item'
					},
					legend : {
						orient : 'vertical',
						x : 'left',
						data : [ '全部数据' ]
					},
					dataRange : {
						x : 'left',
						y : 'bottom',
						splitList : [ {
							start : 1500
						}, {
							start : 900,
							end : 1500
						}, {
							start : 310,
							end : 1000
						}, {
							start : 200,
							end : 300
						}, {
							start : 10,
							end : 200,
							label : '10 到 200（自定义label）'
						}, {
							start : 5,
							end : 5,
							label : '5（自定义特殊颜色）',
							color : 'black'
						}, {
							end : 10
						} ],
						color : [ '#E0022B', '#E09107', '#A3E00B' ]
					},
					toolbox : {
						show : true,
						orient : 'vertical',
						x : 'right',
						y : 'center',
						feature : {
							mark : {
								show : true
							},
							dataView : {
								show : true,
								readOnly : false
							},
							restore : {
								show : true
							},
							saveAsImage : {
								show : true
							}
						}
					},
					roamController : {
						show : true,
						x : 'right',
						mapTypeControl : {
							'china' : true
						}
					},
					series : [ {
						name : '订单量',
						type : 'map',
						mapType : 'china',
						roam : false,
						itemStyle : {
							normal : {
								label : {
									show : true,
									textStyle : {
										color : "rgb(249, 249, 249)"
									}
								}
							},
							emphasis : {
								label : {
									show : true
								}
							}
						},
						data : data
					} ]
				};

				// 为echarts对象加载数据
				dailyActiveChart.setOption(option);

			}
		});
	}

}