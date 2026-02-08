@echo off
echo ============================================
echo   Database Setup for Smart Library
echo   Student ID: KU2534814
echo ============================================
echo.

REM Check if MySQL is running
echo Checking if MySQL is accessible...
mysql -u root -e "SELECT 1;" >nul 2>nul
if %errorlevel% neq 0 (
    echo ERROR: Cannot connect to MySQL.
    echo Please ensure MySQL is running and credentials are correct.
    echo You may need to edit this file to add your MySQL password.
    pause
    exit /b 1
)

echo MySQL connection successful!
echo.
echo Creating database and tables...
mysql -u root < database_setup.sql

if %errorlevel% neq 0 (
    echo ERROR: Database setup failed!
    pause
    exit /b 1
)

echo.
echo Database setup completed successfully!
echo You can now run the application using run.bat
echo.

pause
