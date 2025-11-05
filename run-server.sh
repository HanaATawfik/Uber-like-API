#!/bin/bash

# run-server.sh - Script to run the Ride Sharing Server

echo "================================================"
echo "   Ride Sharing Application - SERVER MODE"
echo "================================================"
echo ""

# Check if Maven wrapper exists
if [ ! -f "./mvnw" ]; then
    echo "Error: Maven wrapper not found!"
    echo "Please run this script from the project root directory."
    exit 1
fi

# Make mvnw executable
chmod +x mvnw

echo "Building the application..."
./mvnw clean package -DskipTests

if [ $? -ne 0 ]; then
    echo ""
    echo "Error: Build failed!"
    exit 1
fi

echo ""
echo "Starting server on port 12345..."
echo "Press Ctrl+C to stop the server"
echo "================================================"
echo ""

# Run the server
./mvnw spring-boot:run

# Alternative: Run from JAR
# java -jar target/ride-sharing-0.0.1-SNAPSHOT.jar