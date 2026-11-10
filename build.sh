#!/bin/bash
echo "Building WorldTeam plugin..."
echo ""

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "Maven is not installed or not in PATH!"
    echo "Please install Maven from https://maven.apache.org/"
    exit 1
fi

# Clean and build the project
echo "Running Maven build..."
mvn clean package

if [ $? -ne 0 ]; then
    echo "Build failed!"
    exit 1
fi

echo ""
echo "Build successful!"
echo "Plugin JAR should be in: target/WorldTeam-1.0.0.jar"