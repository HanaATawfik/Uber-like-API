package com.uber_like.ride_sharing.server.handler;

import com.uber_like.ride_sharing.service.AuthService;
import com.uber_like.ride_sharing.service.RideService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MenuHandler {

    private final AuthService authService;
    private final RideService rideService;
    private final DataInputStream in;
    private final DataOutputStream out;

    public MenuHandler(AuthService authService, RideService rideService,
                      DataInputStream in, DataOutputStream out) {
        this.authService = authService;
        this.rideService = rideService;
        this.in = in;
        this.out = out;
    }

    public void handleCustomerMenu(String username) throws IOException {
        while (true) {
            String choice = in.readUTF();

            switch (choice) {
                case "1": // Request a Ride
                    handleRequestRide(username);
                    break;
                case "2": // View Ride Status
                    handleViewRideStatus(username);
                    break;
                case "3": // Accept Bid
                    handleAcceptBid(username);
                    break;
                case "4": // Disconnect
                    out.writeUTF("Disconnecting...");
                    return;
                default:
                    out.writeUTF("Invalid choice");
            }
        }
    }

    public void handleDriverMenu(String username) throws IOException {
        while (true) {
            String choice = in.readUTF();

            switch (choice) {
                case "1": // Offer Fare
                    handleOfferFare(username);
                    break;
                case "2": // Update Ride Status
                    handleUpdateRideStatus(username);
                    break;
                case "3": // Disconnect
                    out.writeUTF("Disconnecting...");
                    return;
                default:
                    out.writeUTF("Invalid choice");
            }
        }
    }

    private void handleRequestRide(String username) throws IOException {
        String pickupLocation = in.readUTF();
        String dropLocation = in.readUTF();
        String customerFare = in.readUTF();

        RideService.Ride ride = rideService.createRide(pickupLocation, dropLocation, username, customerFare);
        out.writeUTF("Ride requested successfully! Ride ID: " + ride.getRideId());
    }

    private void handleViewRideStatus(String username) throws IOException {
        RideService.Ride ride = rideService.findPendingRideForCustomer(username);

        if (ride != null) {
            out.writeUTF("Ride Status: " + ride.getStatus() +
                        " | Pickup: " + ride.getPickupLocation() +
                        " | Drop: " + ride.getDropLocation());
        } else {
            out.writeUTF("No active ride found");
        }
    }

    private void handleAcceptBid(String username) throws IOException {
        String driverUsername = in.readUTF();
        RideService.Ride ride = rideService.findPendingRideForCustomer(username);

        if (ride != null) {
            ride.setDriverUsername(driverUsername);
            ride.setStatus("ACCEPTED");
            out.writeUTF("Bid accepted! Driver: " + driverUsername);
        } else {
            out.writeUTF("No pending ride found");
        }
    }

    private void handleOfferFare(String username) throws IOException {
        String rideId = in.readUTF();
        String fareOffer = in.readUTF();

        RideService.Ride ride = rideService.getRide(rideId);
        if (ride != null && "PENDING".equals(ride.getStatus())) {
            RideService.Bid bid = new RideService.Bid(username, fareOffer, rideId);
            ride.addBid(bid);
            out.writeUTF("Fare offer submitted successfully");
        } else {
            out.writeUTF("Ride not found or no longer available");
        }
    }

    private void handleUpdateRideStatus(String username) throws IOException {
        String newStatus = in.readUTF();
        RideService.Ride ride = rideService.findActiveRideForDriver(username);

        if (ride != null) {
            ride.setStatus(newStatus);
            out.writeUTF("Ride status updated to: " + newStatus);
        } else {
            out.writeUTF("No active ride found");
        }
    }
}