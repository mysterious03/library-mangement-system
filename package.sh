#!/usr/bin/env bash
# ==============================================================================
# SMART LIBRARY MANAGEMENT SYSTEM - PACKAGING SCRIPT (Linux / macOS / Cloud)
# ==============================================================================
set -e

echo "=================================================================="
echo "  SMART LIBRARY MANAGEMENT SYSTEM - PACKAGING SCRIPT"
echo "=================================================================="

mkdir -p bin

echo "[1/3] Compiling Java source files..."
javac -encoding UTF-8 -cp "lib/h2.jar:lib/mysql-connector-j.jar:src" -d bin $(find src -name "*.java")

echo "[2/3] Writing Manifest..."
cat <<EOF > manifest.txt
Main-Class: Main
Class-Path: lib/h2.jar lib/mysql-connector-j.jar

EOF

echo "[3/3] Creating executable smart-library.jar..."
jar cfm smart-library.jar manifest.txt -C bin .
rm manifest.txt

echo "=================================================================="
echo "[SUCCESS] Package created: smart-library.jar"
echo "To run anywhere:"
echo "  java -jar smart-library.jar"
echo "=================================================================="
