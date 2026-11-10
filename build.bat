@echo off
echo Building WorldTeam plugin...
echo.

REM Check if Maven is installed
where mvn >nul 2>nul
if errorlevel 1 (
    echo Maven is not installed or not in PATH!
    echo Please install Maven from https://maven.apache.org/
    pause
    exit /b 1
)

REM Clean and build the project
echo Running Maven build...
mvn clean package

if errorlevel 1 (
    echo Build failed!
    pause
    exit /b 1
)

echo.
echo Build successful!
echo Plugin JAR should be in: target\WorldTeam-1.0.0.jar
echo.
pause