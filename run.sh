#!/bin/bash
echo "=================================================================="
echo "  SMART LIBRARY MANAGEMENT SYSTEM - APPLICATION LAUNCHER"
echo "=================================================================="

if [ ! -f "bin/Main.class" ]; then
    bash build.sh
fi

echo "Starting Smart Library Management System on http://localhost:8080 ..."
java -cp "bin:lib/h2.jar:lib/mysql-connector-j.jar" Main 8080
