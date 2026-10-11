@echo off
echo ==================================================================
echo   SMART LIBRARY MANAGEMENT SYSTEM - APPLICATION LAUNCHER
echo ==================================================================

where java >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    if exist "C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot\bin\java.exe" (
        set "PATH=C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot\bin;%PATH%"
    ) else if exist "%JAVA_HOME%\bin\java.exe" (
        set "PATH=%JAVA_HOME%\bin;%PATH%"
    )
)

if not exist bin\Main.class (
    echo Bin folder not found or not compiled. Running build first...
    call build.bat
)

echo Starting Smart Library Management System on http://localhost:8080 ...
java -cp "bin;lib/h2.jar;lib/mysql-connector-j.jar" Main 8080
pause
