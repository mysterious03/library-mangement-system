@echo off
echo ==================================================================
echo   SMART LIBRARY MANAGEMENT SYSTEM - PACKAGING SCRIPT
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

echo [1/3] Compiling Java source files...
javac -encoding UTF-8 -cp "lib/h2.jar;lib/mysql-connector-j.jar;src" -d bin src/model/*.java src/dao/*.java src/service/*.java src/util/*.java src/controller/*.java src/database/*.java src/Main.java

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed!
    exit /b 1
)

echo [2/3] Writing Manifest...
echo Main-Class: Main > manifest.txt
echo Class-Path: lib/h2.jar lib/mysql-connector-j.jar >> manifest.txt
echo. >> manifest.txt

echo [3/3] Creating executable smart-library.jar...
jar cfm smart-library.jar manifest.txt -C bin .

del manifest.txt

echo ==================================================================
echo [SUCCESS] Package created: smart-library.jar
echo To run anywhere with Java 17+:
echo   java -jar smart-library.jar
echo ==================================================================
