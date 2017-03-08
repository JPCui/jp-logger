@echo off
rem /**
rem  * Copyright &copy; 2012-2016 51cth.com All rights reserved.
rem  *
rem  * Author: Cui
rem  */
title %cd%
echo.
echo [信息] 打包  - package
echo.

rem pause
rem echo.

cd %~dp0
cd..

call mvn clean compile deploy -Dmaven.test.skip=true

cd bin
pause