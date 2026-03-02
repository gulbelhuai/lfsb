@echo off
chcp 65001 >nul

REM 统一走根目录 start.bat（已包含 JDK17 检测、.env 读取、动态查找 jar 等逻辑）
cd /d %~dp0\..
call start.bat
exit /b %errorlevel%
