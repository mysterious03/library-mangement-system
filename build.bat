@echo off
echo ==================================================================
echo   SMART LIBRARY MANAGEMENT SYSTEM - COMPILATION SCRIPT
echo ==================================================================

where javac >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    if exist "C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot\bin\javac.exe" (
        set "PATH=C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot\bin;%PATH%"
    ) else if exist "%JAVA_HOME%\bin\javac.exe" (
        set "PATH=%JAVA_HOME%\bin;%PATH%"
    )
)

if not exist bin mkdir bin

echo Compiling Java source files...
javac -encoding UTF-8 -cp "lib/h2.jar;lib/mysql-connector-j.jar;src" -d bin src/model/*.java src/dao/*.java src/service/*.java src/util/*.java src/controller/*.java src/database/*.java src/Main.java

if %ERRORLEVEL% EQU 0 (
    echo [SUCCESS] Build succeeded! Compiled classes stored in bin/
) else (
    echo [ERROR] Build failed! Check compiler output above.
)
pause
