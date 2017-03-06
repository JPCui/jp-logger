<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="http://apps.bdimg.com/libs/bootstrap/bootstrap-3.3.4.min.css">
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
    <title>Log Category</title>
</head>
<body>

<div ng-app="tipAnimate" ng-controller="tipAController">
    <div class="C-tip half-circle" ng-click="changeTip()"></div>
    <div class="C-cate" ng-hide="A_hide">
        <a href="${serverPath}/log/visit">VISIT</a> | 
        <a href="${serverPath}/log/info">INFO</a> | 
        <a href="${serverPath}/log/warn">WARN</a> | 
        <a href="${serverPath}/log/error">ERROR</a> | 
        <a href="${serverPath}/log/inspector">BeanInspector</a>
    </div>
</div>


<script src="http://apps.bdimg.com/libs/angular.js/1.4.6/angular.min.js"></script>
<script src="http://cdn.static.runoob.com/libs/angular.js/1.4.6/angular-animate.min.js"></script>
<script>
    var appTip = angular.module("tipAnimate", ["ngAnimate"]);
    appTip.controller("tipAController", function ($scope) {
        $scope.changeTip = function () {
            $scope.A_hide = !$scope.A_hide;
        };
    });
</script>
</body>
</html>