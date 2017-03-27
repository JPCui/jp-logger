<#include "../common.ftl">
<!DOCTYPE html>
<html lang="en">
<head>
<@common_head title="日志集中管理系统">

<style type="text/css">
    ul.t-table, ul.t-table li {
        list-style: none;
        padding: 0;
        margin: 0 0 0 25px;
        -webkit-margin-before: 0;
        -webkit-margin-after: 0;
        -webkit-padding-start: 0;
        width: 90%;
    }
    ul.t-table li{
        margin-top: 10px;
    }

    .t-op {
        width: 30px;
    }

    .t-1{
        width: 80px;
    }

    .tr-deep-1 {
        background-color: silver;
        border-top: solid 2px black;
    }
    
    .long-l {
    	width: 700px;
    	overflow: overlay;
    }
    
    .long-m {
    	width: 150px;
    	overflow: overlay;
    }

    @media only screen and (min-width: 888px) {
        .t-hide-on-narrow {
            display: table-cell !important;
        }
    }

    .t-hide-on-narrow {
        display: none;
    }
</style>
</@common_head>
</head>
<body id="demo" ng-app="demo" ng-controller="demo">
<@common_body>
<h1 style="text-align: center;">bean inspector</h1>
<header>
	<table class="table" style="text-align: center;">
		<tr>
		<td>
			<a ng-if="prevPage!=pageNum" ng-click="ePrevPage()" href="#">上一页</a>
			<a ng-if="prevPage==pageNum">&nbsp;</a>
		</td>
		<td>
			<a ng-if="nextPage!=pageNum" ng-click="eNextPage()" href="#">下一页</a>
			<a ng-if="nextPage==pageNum">&nbsp;</a>
		</td>
		<td>排序 : <input ng-model="sortedName"/></td>
		<td>页码 : <input ng-model="pageNum"/></td>
		</tr>
		<tr ng-hide="B_hide">
		<td colspan="2">
			loading...
		</td>
		</tr>
	</table>
</header>
<div>
	<table class="table" style="margin: 0 auto;">
		<thead>
	    <tr class="t-tr">
	        <td class="t-td t_op">[+]</td>
	        <td class="t-td t-1">avg period</td>
	        <td class="t-td t-x">function(...args)</td>
	        <td class="t-td t-1 t-hide-on-narrow">calledTimes</td>
	        <td class="t-td t-1 t-hide-on-narrow">period</td>
	        <td class="t-td t-1 t-hide-on-narrow">return line num</td>
	    </tr>
		</thead>
	    <tbody ng-repeat="node in nodes" ng-include="'treeView'"></tbody>
	</table>
</div>
</@common_body>

<@common_bottom>
<script src="http://apps.bdimg.com/libs/angular.js/1.4.6/angular.min.js"></script>
<script src="http://apps.bdimg.com/libs/angular.js/1.4.6/angular-animate.min.js"></script>
<script src="http://apps.bdimg.com/libs/jquery/2.1.4/jquery.min.js"></script>
<script src="http://apps.bdimg.com/libs/bootstrap/3.3.4/js/bootstrap.min.js"></script>
<!-- recursion -->
<script id="treeView" type="text/ng-template">
<tr class="tr-deep-{{deep%2}}" style="border-top: solid 2px black;">
    <td ng-hide="true" ng-init="deep = deep +1"></td>
    <td class="t-op">{{deep}}</td>
    <td class="">{{node.bean.avgPeriod}}</td>
    <td class="">{{node.bean.clazz}}.{{node.bean.method}}</td>
    <td class=" t-hide-on-narrow">{{node.bean.calledTimes}}</td>
    <td class=" t-hide-on-narrow">{{node.bean.period}}</td>
    <td class=" t-hide-on-narrow">{{node.bean.returnLineNum}}</td>
</tr>
<tr class="tr-deep-{{deep%2}} t-th" ng-repeat="node in node.childs" ng-include="'treeView'">
</tr>
</script>
<script>

    var app = angular.module('demo', []);
    
	app.controller('demo', function($scope, $http) {
		$scope.B_hide = 0;
		$scope.deep = 0;
		$scope.sortedName = "bean.avgPeriod";
		$scope.pageNum = 1;
	    $scope.request = function(pageNum) {
			
    		$http.get("${serverPath}/log/inspector.json?sortedName=" + $scope.sortedName + "&_pageNum=" + $scope.pageNum).success(
   				function(response) {
					$scope.pageNum = response.data.currPage;
					$scope.currPage = response.data.currPage;
					$scope.prevPage = response.data.prevPage;
					$scope.nextPage = response.data.nextPage;
					$scope.nodes = response.data.resultList;
					$scope.B_hide = 1;
   			});
	    }
        $scope.eNextPage = function() {
        	$scope.request($scope.nextPage);
        }
        $scope.ePrevPage = function() {
        	$scope.request($scope.prevPage);
        }
        $scope.request($scope.pageNum);
	    $scope.changeTip = function () {
	        $scope.A_hide = !$scope.A_hide;
	    };
	});
	// 页面加载完成后,再加载模块
	// angular.element(document).ready(function() {
	// 	angular.bootstrap(document.getElementById("demo"), [ 'demo' ]);
	// });
</script>
</@common_bottom>
</body>
</html>