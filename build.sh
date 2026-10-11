#!/bin/bash
echo "=================================================================="
echo "  SMART LIBRARY MANAGEMENT SYSTEM - COMPILATION SCRIPT"
echo "=================================================================="

mkdir -p bin

echo "Compiling Java source files..."
javac -encoding UTF-8 -cp "lib/h2.jar:lib/mysql-connector-j.jar:src" -d bin src/model/*.java src/dao/*.java src/service/*.java src/util/*.java src/controller/*.java src/database/*.java src/Main.java

if [ $? -eq 0 ]; then
    echo "[SUCCESS] Build succeeded! Classes compiled to bin/"
else
    echo "[ERROR] Compilation failed!"
fi
