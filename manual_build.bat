@echo off
echo Building WorldTeam plugin manually...
echo.

REM Create output directory
if not exist "target\classes" mkdir target\classes
if not exist "target" mkdir target

REM Check Java compiler
where javac >nul 2>nul
if errorlevel 1 (
    echo Java compiler not found!
    echo Please install JDK 8 or later
    pause
    exit /b 1
)

REM Compile Java files
echo Compiling Java source files...
javac -cp ".;lib/*" -d target\classes ^
    src\main\java\com\altergen\worldteam\*.java

if errorlevel 1 (
    echo Compilation failed!
    pause
    exit /b 1
)

REM Create JAR file
echo Creating JAR file...
cd target\classes
jar cf ..\WorldTeam.jar com\altergen\worldteam\*.class
cd ..\..

REM Copy resources
echo Adding resources to JAR...
cd src\main\resources
jar uf ..\..\..\target\WorldTeam.jar *
cd ..\..\..

echo.
echo Build successful!
echo Plugin JAR: target\WorldTeam.jar
echo.
pause