package com.uber_like.ride_sharing.server.handler;

import com.uber_like.ride_sharing.service.AuthService;
import com.uber_like.ride_sharing.service.RideService;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private final AuthService authService;
    private final RideService rideService;
    private DataInputStream in;
    private DataOutputStream out;
    private volatile boolean running = true;
    private String currentUsername;
    private boolean isCustomer;

    public ClientHandler(Socket socket, AuthService authService, RideService rideService) {
        this.clientSocket = socket;
        this.authService = authService;
        this.rideService = rideService;
    }

    @Override
    public void run() {
        try {
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());

            // 1. Handle main menu choice (Customer=1, Driver=2)
            String mainChoice = in.readUTF();
            isCustomer = mainChoice.equals("1");

            // 2. Handle auth menu choice (Signup=1, Login=2)
            String authChoice = in.readUTF();

            boolean authenticated;
            if (authChoice.equals("1")) {
                authenticated = handleSignup();
            } else {
                authenticated = handleLogin();
            }

            // 3. If authenticated, handle menu operations
            if (authenticated) {
                if (isCustomer) {
                    handleCustomerOperations();
                } else {
                    handleDriverOperations();
                }
            }

        } catch (IOException e) {
            System.out.println("Client disconnected: " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    private boolean handleSignup() throws IOException {
        String email = in.readUTF();
        String username = in.readUTF();
        String password = in.readUTF();

        boolean success;
        if (isCustomer) {
            success = authService.registerCustomer(email, username, password);
        } else {
            success = authService.registerDriver(email, username, password);
        }

        if (success) {
            currentUsername = username;
            out.writeUTF("SUCCESS: Registration successful!");
        } else {
            out.writeUTF("FAILURE: Username already exists");
        }
        out.flush();

        return success;
    }

    private boolean handleLogin() throws IOException {
        String username = in.readUTF();
        String password = in.readUTF();

        boolean success;
        if (isCustomer) {
            AuthService.Customer customer = authService.loginCustomer(username, password);
            success = (customer != null);
        } else {
            AuthService.Driver driver = authService.loginDriver(username, password);
            success = (driver != null);
        }

        if (success) {
            currentUsername = username;
            out.writeUTF("SUCCESS: Login successful!");
        } else {
            out.writeUTF("FAILURE: Invalid credentials");
        }
        out.flush();

        return success;
    }

    private void handleCustomerOperations() throws IOException {
        while (running) {
            String choice = in.readUTF();

            switch (choice) {
                case "1": // Request a Ride
                    handleRequestRide();
                    break;
                case "2": // View Ride Status
                    handleViewRideStatus();
                    break;
                case "3": // Accept Bid
                    handleAcceptBid();
                    break;
                case "4": // Exit
                    running = false;
                    break;
            }
        }
    }

    private void handleDriverOperations() throws IOException {
        while (running) {
            String choice = in.readUTF();

            switch (choice) {
                case "1": // Offer Fare
                    handleOfferFare();
                    break;
                case "2": // Update Ride Status
                    handleUpdateRideStatus();
                    break;
                case "3": // Exit
                    running = false;
                    break;
            }
        }
    }

    private void handleRequestRide() throws IOException {
        String pickupLocation = in.readUTF();
        String dropLocation = in.readUTF();
        String fare = in.readUTF();

        RideService.Ride ride = rideService.createRide(pickupLocation, dropLocation, currentUsername, fare);
        out.writeUTF("Ride requested successfully! Ride ID: " + ride.getRideId());
        out.flush();
    }

    private void handleViewRideStatus() throws IOException {
        RideService.Ride ride = rideService.findPendingRideForCustomer(currentUsername);

        if (ride != null) {
            out.writeUTF("Current Ride - Status: " + ride.getStatus() +
                    ", Pickup: " + ride.getPickupLocation() +
                    ", Drop: " + ride.getDropLocation() +
                    (ride.getDriverUsername() != null ? ", Driver: " + ride.getDriverUsername() : ""));
        } else {
            out.writeUTF("No active ride found");
        }
        out.flush();
    }

    private void handleAcceptBid() throws IOException {
        String driverUsername = in.readUTF();
        RideService.Ride ride = rideService.findPendingRideForCustomer(currentUsername);

        if (ride != null) {
            ride.setDriverUsername(driverUsername);
            ride.setStatus("ACCEPTED");

            AuthService.Driver driver = authService.getDriver(driverUsername);
            if (driver != null) {
                driver.setStatus("busy");
            }

            out.writeUTF("SUCCESS: Bid accepted! Driver " + driverUsername + " has been assigned.");
        } else {
            out.writeUTF("FAILURE: No pending ride found");
        }
        out.flush();
    }

    private void handleOfferFare() throws IOException {
        AuthService.Driver driver = authService.getDriver(currentUsername);

        if (driver == null || !"available".equals(driver.getStatus())) {
            out.writeUTF("FAILURE: You are not available to accept rides");
            out.flush();
            return;
        }

        out.writeUTF("SUCCESS: You are available");
        out.flush();

        List<RideService.Ride> pendingRides = rideService.getPendingRides();

        if (pendingRides.isEmpty()) {
            out.writeUTF("INFO: No ride requests available at the moment");
            out.flush();
            return;
        }

        StringBuilder ridesData = new StringBuilder("RIDE_REQUESTS:");
        for (RideService.Ride ride : pendingRides) {
            ridesData.append("Ride ID: ").append(ride.getRideId())
                    .append(", Pickup: ").append(ride.getPickupLocation())
                    .append(", Drop: ").append(ride.getDropLocation())
                    .append(", Customer Fare: $").append(ride.getCustomerFare())
                    .append("|");
        }
        out.writeUTF(ridesData.toString());
        out.flush();

        String choice = in.readUTF();
        int rideIndex;
        try {
            rideIndex = Integer.parseInt(choice) - 1;
        } catch (NumberFormatException e) {
            out.writeUTF("FAILURE: Invalid input");
            out.flush();
            return;
        }

        if (rideIndex == -1) {
            out.writeUTF("INFO: Bid cancelled");
            out.flush();
            return;
        }

        if (rideIndex < 0 || rideIndex >= pendingRides.size()) {
            out.writeUTF("FAILURE: Invalid choice");
            out.flush();
            return;
        }

        RideService.Ride selectedRide = pendingRides.get(rideIndex);
        out.writeUTF("Selected ride: " + selectedRide.getRideId());
        out.flush();

        String fareOffer = in.readUTF();
        RideService.Bid bid = new RideService.Bid(currentUsername, fareOffer, selectedRide.getRideId());
        selectedRide.addBid(bid);

        out.writeUTF("SUCCESS: Your bid of $" + fareOffer + " has been submitted!");
        out.flush();

        // Send bid notification to customer
        notifyCustomerOfBid(selectedRide.getCustomerUsername(), currentUsername, fareOffer, selectedRide.getRideId());
    }

    private void handleUpdateRideStatus() throws IOException {
        RideService.Ride ride = rideService.findActiveRideForDriver(currentUsername);

        if (ride == null) {
            out.writeUTF("INFO: You have no active ride");
            out.flush();
            return;
        }

        out.writeUTF("RIDE_STATUS_MENU:Current Status: " + ride.getStatus());
        out.flush();

        String statusChoice = in.readUTF();
        String newStatus;

        switch (statusChoice) {
            case "1":
                newStatus = "PICKED_UP";
                break;
            case "2":
                newStatus = "COMPLETED";
                AuthService.Driver driver = authService.getDriver(currentUsername);
                if (driver != null) {
                    driver.setStatus("available");
                }
                break;
            case "3":
                out.writeUTF("INFO: Status update cancelled");
                out.flush();
                return;
            default:
                out.writeUTF("FAILURE: Invalid choice");
                out.flush();
                return;
        }

        ride.setStatus(newStatus);
        out.writeUTF("SUCCESS: Ride status updated to " + newStatus);
        out.flush();
    }

    private void notifyCustomerOfBid(String customerUsername, String driverUsername, String fareOffer, String rideId) {
        // This would require maintaining a map of active client handlers
        // For now, this is a placeholder showing where async notifications would go
        System.out.println("Would notify " + customerUsername + " of bid from " + driverUsername);
    }

    private void cleanup() {
        running = false;
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null) clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
