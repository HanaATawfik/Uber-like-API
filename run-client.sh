#!/bin/bash

# run-client.sh - Script to run the Ride Sharing Client

echo "================================================"
echo "   Ride Sharing Application - CLIENT MODE"
echo "================================================"
echo ""

# Default values
SERVER_HOST=${1:-localhost}
SERVER_PORT=${2:-12345}

echo "Server: $SERVER_HOST"
echo "Port: $SERVER_PORT"
echo ""

# Check if JAR exists
JAR_FILE="target/ride-sharing-0.0.1-SNAPSHOT.jar"

if [ ! -f "$JAR_FILE" ]; then
    echo "JAR file not found. Building the application..."

    # Make mvnw executable
    chmod +x mvnw

    ./mvnw clean package -DskipTests

    if [ $? -ne 0 ]; then
        echo ""
        echo "Error: Build failed!"
        exit 1
    fi
fi

echo "Connecting to server..."
echo "================================================"
echo ""

# Run the client
java -jar "$JAR_FILE" --client "$SERVER_HOST" "$SERVER_PORT"