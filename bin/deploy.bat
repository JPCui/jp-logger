@echo off
rem /**
rem  * Copyright &copy; 2012-2016 https://github.com/jpcui All rights reserved.
rem  *
rem  * Author: Cui
rem  */
title %cd%
echo.
echo [信息] 部署到远程仓库
echo.

rem pause
rem echo.

cd %~dp0
cd..

call mvn clean deploy -Dmaven.test.skip=true

cd bin
pause