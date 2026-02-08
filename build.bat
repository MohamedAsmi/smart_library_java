@echo off
echo ============================================
echo   Building Smart Library JAR File
echo   Student ID: KU2534814
echo ============================================
echo.

REM Navigate to project directory
cd /d "%~dp0"

echo Building JAR with Maven...
call mvn clean package

if %errorlevel% neq 0 (
    echo ERROR: Build failed!
    pause
    exit /b 1
)

echo.
echo JAR file created successfully in target folder!
echo You can run it with: java -jar target/smart-library-1.0-SNAPSHOT.jar
echo.

pause
