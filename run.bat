@echo off
echo ============================================
echo   Smart Library Management System
echo   Student ID: KU2534814
echo ============================================
echo.

REM Check if Maven is installed
where mvn >nul 2>nul
if %errorlevel% neq 0 (
    echo ERROR: Maven is not installed or not in PATH
    echo Please install Maven from https://maven.apache.org/
    pause
    exit /b 1
)

REM Navigate to project directory
cd /d "%~dp0"

echo Building the project with Maven...
call mvn clean compile

if %errorlevel% neq 0 (
    echo ERROR: Build failed!
    pause
    exit /b 1
)

echo.
echo Starting the application...
call mvn exec:java -Dexec.mainClass="com.library.KU2534814Main"

pause
