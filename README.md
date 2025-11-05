# Ride Sharing Application - Complete Setup Guide

## 📋 Table of Contents
- [Prerequisites](#prerequisites)
- [Project Structure](#project-structure)
- [Installation Steps](#installation-steps)
- [Running the Application](#running-the-application)
- [Testing the Application](#testing-the-application)
- [Troubleshooting](#troubleshooting)
- [Features Implemented](#features-implemented)

## 🔧 Prerequisites

- **Java 17 or higher** (check with `java -version`)
- **Maven 3.6+** (included via Maven Wrapper)
- Terminal/Command Prompt access
- Multiple terminal windows for testing

## 📁 Project Structure

```
ride-sharing/
├── src/
│   └── main/
│       └── java/
│           └── com/uber_like/ride_sharing/
│               ├── RideSharingApplication.java    # Main application entry point
│               ├── client/
│               │   ├── RideClient.java            # Client main logic
│               │   ├── ServerListener.java        # Handles server messages
│               │   └── menu/
│               │       └── Menu.java              # Client menu system
│               └── server/
│                   ├── RideServer.java            # Server main logic
│                   ├── handler/
│                   │   ├── ClientHandler.java     # Handles each client connection
│                   │   └── MenuHandler.java       # Server-side menu handling
│                   └── model/
│                       ├── Customer.java          # Customer data model
│                       ├── Driver.java            # Driver data model
│                       ├── Ride.java              # Ride data model
│                       └── Bid.java               # Bid data model
├── pom.xml                                        # Maven configuration
├── mvnw / mvnw.cmd                                # Maven wrapper scripts
├── run-server.sh / run-server.cmd                 # Server launch scripts
└── run-client.sh / run-client.cmd                 # Client launch scripts
```

## 📥 Installation Steps

### Step 1: Copy All Files

Copy all the provided Java files into their respective directories according to the project structure above.

### Step 2: Make Scripts Executable (Mac/Linux Only)

```bash
chmod +x mvnw
chmod +x run-server.sh
chmod +x run-client.sh
```

### Step 3: Verify Java Installation

```bash
java -version
```

You should see Java 17 or higher.

## 🚀 Running the Application

### Option A: Using Shell Scripts (Recommended)

#### On Mac/Linux:

**Terminal 1 - Start Server:**
```bash
./run-server.sh
```

**Terminal 2 - Start First Client:**
```bash
./run-client.sh
```

**Terminal 3 - Start Second Client:**
```bash
./run-client.sh
```

#### On Windows:

**Command Prompt 1 - Start Server:**
```cmd
run-server.cmd
```

**Command Prompt 2 - Start First Client:**
```cmd
run-client.cmd
```

**Command Prompt 3 - Start Second Client:**
```cmd
run-client.cmd
```

### Option B: Using Maven Commands Directly

#### Start Server:
```bash
./mvnw spring-boot:run
```

#### Start Client:
```bash
./mvnw clean package -DskipTests
java -jar target/ride-sharing-0.0.1-SNAPSHOT.jar --client localhost 12345
```

### Option C: Connect to Remote Server

If the server is running on a different machine:

**Mac/Linux:**
```bash
./run-client.sh <server-ip> <port>
# Example: ./run-client.sh 192.168.1.100 12345
```

**Windows:**
```cmd
run-client.cmd <server-ip> <port>
REM Example: run-client.cmd 192.168.1.100 12345
```

## 🧪 Testing the Application

### Complete Test Scenario

Follow these steps to test all features:

#### 1. Start the Server
Open Terminal 1 and run the server.

#### 2. Start Customer Client
Open Terminal 2:
1. Choose "Customer"
2. Choose "Sign up"
3. Enter email: customer1@test.com
4. Enter username: customer1
5. Enter password: pass123

#### 3. Request a Ride (Customer)
1. Choose "Request a Ride"
2. Enter pickup: Downtown
3. Enter destination: Airport
4. Enter fare: 25

#### 4. Start Driver Client 1
Open Terminal 3:
1. Choose "Driver"
2. Choose "Sign up"
3. Enter email: driver1@test.com
4. Enter username: driver1
5. Enter password: pass123

#### 5. Driver 1 Makes an Offer
1. Choose "Offer Ride Fare"
2. Select the available ride (enter 1)
3. Enter fare offer: 22

#### 6. Start Driver Client 2
Open Terminal 4:
1. Choose "Driver"
2. Choose "Sign up"
3. Username: driver2
4. Make another offer: 20

#### 7. Customer Accepts a Bid
Switch to Terminal 2 (Customer):
1. You should see bid notifications
2. Choose "Accept Driver Bid"
3. Select a bid (enter 1 or 2)

#### 8. Driver Updates Ride Status
Switch to the accepted driver's terminal:
1. Choose "Update Ride Status"
2. Choose "Start Ride"
3. Wait a moment
4. Choose "Update Ride Status" again
5. Choose "Complete Ride"

#### 9. Verify Notifications
- Customer should receive ride status updates
- Other drivers should be notified that ride was assigned to someone else

## 🔍 Troubleshooting

### Issue: "ClassNotFoundException"

**Solution:** Make sure you're in the project root directory and have built the project:
```bash
./mvnw clean package -DskipTests
```

### Issue: "Port already in use"

**Solution:** The server port 12345 is already in use. Either:
1. Stop the existing process
2. Change the port in `src/main/resources/application.properties`:
   ```properties
   socket.server.port=12346
   ```

### Issue: "Connection refused"

**Solution:**
1. Make sure the server is running first
2. Check firewall settings
3. Verify the correct server address and port

### Issue: Maven wrapper not found

**Solution:**
```bash
# Mac/Linux
chmod +x mvnw

# Windows - download Maven wrapper
mvn -N io.takari:maven:wrapper
```

### Issue: Build fails with "invalid target release"

**Solution:** Your Java version is too old. Install Java 17 or higher:
- Download from: https://adoptium.net/

### Issue: "Could not find or load main class"

**Solution:** You need to build the project first:
```bash
./mvnw clean package
```

## ✨ Features Implemented

### Core Features (Team of 2)
- ✅ Server-Client Communication using Java Sockets
- ✅ User Authentication (Login & Registration)
- ✅ Ride Request System
- ✅ Driver Availability Management
- ✅ Ride Assignment
- ✅ Ride Status Updates (Start/Complete)
- ✅ Graceful Disconnect
- ✅ Multithreading (Server and Client)
- ✅ Concurrent Client Handling
- ✅ Bidding System
- ✅ Real-time Notifications

### Technical Implementation
- Spring Boot integration for server
- Concurrent data structures (ConcurrentHashMap)
- Thread-safe operations
- Blocking queues for synchronous responses
- Asynchronous notification system
- Error handling and validation
- Clean architecture with separation of concerns

## 📝 Usage Examples

### Customer Workflow
```
1. Sign up/Login
2. Request a ride
3. Wait for driver bids
4. Accept a bid
5. Monitor ride status
6. Disconnect when done
```

### Driver Workflow
```
1. Sign up/Login
2. View available rides
3. Make a bid on a ride
4. Wait for customer acceptance
5. Start the ride
6. Complete the ride
7. Become available for next ride
```

## 🎯 Assignment Compliance

This implementation fulfills all requirements from the assignment:
- Java SE sockets ✅
- Multithreading ✅
- Customer and Driver clients ✅
- Ride request and bidding ✅
- Status updates ✅
- Multiple concurrent clients ✅
- Text-based interface ✅
- Proper error handling ✅

## 📞 Support

If you encounter any issues:
1. Check the troubleshooting section
2. Verify all files are in correct locations
3. Ensure Java 17+ is installed
4. Try running `./mvnw clean package -DskipTests` again

## 🎓 Development Notes

- Built with Java 17
- Uses Spring Boot 3.4.11
- Maven for dependency management
- Follows assignment specifications exactly
- No GUI - pure command line interface
- Ready for demonstration and testing