@echo off
chcp 65001 >nul
echo.
echo ========================================
echo   县级养老补贴发放系统 - 启动脚本
echo ========================================
echo.

REM 切换到项目根目录
cd /d %~dp0

REM 读取.env文件中的环境变量
if exist ".env" (
    echo [信息] 正在读取 .env 文件中的环境变量...
    for /f "usebackq eol=# tokens=1,2 delims==" %%a in (".env") do (
        if not "%%a"=="" (
            set "%%a=%%b"
        )
    )
    echo [信息] 环境变量加载完成
    echo.
) else (
    echo [警告] 未找到 .env 文件，使用默认配置
    echo.
)

REM 检查jar文件是否存在（支持动态查找最新的jar文件）
set JAR_FILE=
for %%f in (ruoyi-admin\target\shebao-server-*.jar) do set JAR_FILE=%%f

if not exist "%JAR_FILE%" (
    echo [错误] 找不到jar文件
    echo [提示] 请先执行 mvn clean package -Dmaven.test.skip=true 进行打包
    pause
    exit /b 1
)

echo [信息] 找到jar文件: %JAR_FILE%
echo.

REM 查找Java 17或更高版本
set JAVA_CMD=
if defined JAVA_HOME (
    REM 兼容 JAVA_HOME 写成 "C:\Path With Spaces" 的情况
    set "JAVA_HOME=%JAVA_HOME:"=%"
    if exist "%JAVA_HOME%\bin\java.exe" (
        set "JAVA_CMD=%JAVA_HOME%\bin\java.exe"
        echo [信息] 使用 JAVA_HOME 中的Java: %JAVA_HOME%
    )
)

REM 如果JAVA_HOME未设置或不存在，尝试使用系统PATH中的java
if not defined JAVA_CMD (
    where java >nul 2>&1
    if %errorlevel% equ 0 (
        for /f "delims=" %%i in ('where java') do (
            set JAVA_CMD=%%i
            echo [信息] 使用系统PATH中的Java: %%i
            goto :check_java_version
        )
    )
)

:check_java_version
if not defined JAVA_CMD (
    echo [错误] 未找到Java，请确保已安装Java 17或更高版本
    echo [提示] 可以设置JAVA_HOME环境变量指向Java 17安装目录
    pause
    exit /b 1
)

REM 确保 JAVA_CMD 不带引号（避免 ""C:\Program Files\..."" 这类路径导致执行失败）
set "JAVA_CMD=%JAVA_CMD:"=%"

REM 检查Java版本（需要Java 17+）
echo [信息] 正在检查Java版本...
set "JAVA_VER="
for /f "tokens=1,2,3" %%a in ('"%JAVA_CMD%" -version 2^>^&1') do (
    if /i "%%a"=="java" if /i "%%b"=="version" (
        set "JAVA_VER=%%~c"
        goto :got_java_ver
    )
    if /i "%%a"=="openjdk" if /i "%%b"=="version" (
        set "JAVA_VER=%%~c"
        goto :got_java_ver
    )
)
:got_java_ver
if not defined JAVA_VER (
    echo [错误] 无法获取Java版本信息
    pause
    exit /b 1
)
set "JAVA_MAJOR="
for /f "tokens=1 delims=." %%m in ("%JAVA_VER%") do set "JAVA_MAJOR=%%m"
if "%JAVA_MAJOR%"=="1" for /f "tokens=2 delims=." %%m in ("%JAVA_VER%") do set "JAVA_MAJOR=%%m"
if %JAVA_MAJOR% LSS 17 (
    echo [错误] 当前Java版本为 %JAVA_VER%（主版本 %JAVA_MAJOR%），本项目需要 Java 17 或更高版本
    echo [提示] 请安装/切换到 JDK 17
    echo [提示] 建议设置 JAVA_HOME 指向 JDK 17，并将 %%JAVA_HOME%%\bin 置于 PATH 前面
    pause
    exit /b 1
)

REM 设置JVM参数
set JAVA_OPTS=-Xms512m -Xmx1024m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m

REM 显示配置信息
echo [信息] 启动参数: %JAVA_OPTS%
echo [信息] 服务端口: 8087
echo [信息] 访问路径: http://localhost:8087/api
if defined MYSQL_HOST (
    echo [信息] 数据库主机: %MYSQL_HOST%
    echo [信息] 数据库端口: %MYSQL_PORT%
    echo [信息] 数据库名称: %MYSQL_DB%
)
if defined REDIS_HOST (
    echo [信息] Redis主机: %REDIS_HOST%
)
echo.
echo [信息] 正在启动服务...
echo.

REM 启动服务
"%JAVA_CMD%" %JAVA_OPTS% -jar "%JAR_FILE%"

pause
