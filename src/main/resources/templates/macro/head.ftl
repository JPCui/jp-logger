<#macro head title>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="http://cdn.bootcss.com/bootstrap/3.3.4/css/bootstrap.min.css">
    <#nested>
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
    <title>${title}</title>
</#macro>