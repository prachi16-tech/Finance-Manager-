@echo off
setlocal enabledelayedexpansion
title Personal Finance Manager - Server

echo =================================================================
echo           PERSONAL FINANCE MANAGER - WEB APPLICATION
echo =================================================================
echo.

cd /d "%~dp0"

echo [1/3] Checking Java runtime environment...
java -version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Java is not recognized on your system PATH!
    echo Please install JDK 11 or JDK 17 and try again.
    pause
    exit /b 1
)

echo [2/3] Preparing application classes and dependencies...
if not exist "target\classes\com\finance\AppRunner.class" (
    echo Compiling Java source files...
    if not exist "target\classes" mkdir "target\classes"
    javac -encoding UTF-8 -cp "lib/*" -d "target/classes" src/main/java/com/finance/*.java src/main/java/com/finance/*/*.java
)

echo [3/3] Launching Tomcat Server on http://localhost:8080/ ...
echo.
echo =================================================================
echo   Application URL: http://localhost:8080/
echo   Demo Login:      mayur@example.com
echo   Demo Password:   Password@123
echo =================================================================
echo.

:: Automatically open browser after 2 seconds in background
start "" cmd /c "timeout /t 2 /nobreak >nul & start http://localhost:8080/"

:: Start Embedded Tomcat server
java -cp "target/classes;lib/*" com.finance.AppRunner

pause
