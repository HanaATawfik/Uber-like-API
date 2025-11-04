package com.uber_like.ride_sharing.service;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RideService {

    private static final AtomicInteger rideIdCounter = new AtomicInteger(1);
    private final ConcurrentHashMap<String, Ride> rides = new ConcurrentHashMap<>();

    public Ride createRide(String pickupLocation, String dropLocation,
                          String customerUsername, String customerFare) {
        String rideId = "RIDE" + rideIdCounter.getAndIncrement();
        Ride ride = new Ride(rideId, pickupLocation, dropLocation, customerUsername, customerFare);
        rides.put(rideId, ride);
        return ride;
    }

    public Ride getRide(String rideId) {
        return rides.get(rideId);
    }

    public List<Ride> getPendingRides() {
        List<Ride> pendingRides = new ArrayList<>();
        for (Ride ride : rides.values()) {
            if ("PENDING".equals(ride.getStatus())) {
                pendingRides.add(ride);
            }
        }
        return pendingRides;
    }

    public Ride findPendingRideForCustomer(String customerUsername) {
        for (Ride ride : rides.values()) {
            if (ride.getCustomerUsername().equals(customerUsername) &&
                "PENDING".equals(ride.getStatus())) {
                return ride;
            }
        }
        return null;
    }

    public Ride findActiveRideForDriver(String driverUsername) {
        for (Ride ride : rides.values()) {
            if (driverUsername.equals(ride.getDriverUsername()) &&
                !"COMPLETED".equals(ride.getStatus())) {
                return ride;
            }
        }
        return null;
    }

    // Inner classes
    public static class Ride {
        private final String rideId;
        private final String pickupLocation;
        private final String dropLocation;
        private final String customerUsername;
        private String driverUsername;
        private final String customerFare;
        private String status = "PENDING";
        private final List<Bid> bids = Collections.synchronizedList(new ArrayList<>());

        public Ride(String rideId, String pickupLocation, String dropLocation,
                   String customerUsername, String customerFare) {
            this.rideId = rideId;
            this.pickupLocation = pickupLocation;
            this.dropLocation = dropLocation;
            this.customerUsername = customerUsername;
            this.customerFare = customerFare;
        }

        public String getRideId() { return rideId; }
        public String getPickupLocation() { return pickupLocation; }
        public String getDropLocation() { return dropLocation; }
        public String getCustomerUsername() { return customerUsername; }
        public String getDriverUsername() { return driverUsername; }
        public void setDriverUsername(String driverUsername) { this.driverUsername = driverUsername; }
        public String getCustomerFare() { return customerFare; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public List<Bid> getBids() { return bids; }
        public void addBid(Bid bid) { bids.add(bid); }
    }

    public static class Bid {
        private final String driverUsername;
        private final String fareOffer;
        private final String rideId;

        public Bid(String driverUsername, String fareOffer, String rideId) {
            this.driverUsername = driverUsername;
            this.fareOffer = fareOffer;
            this.rideId = rideId;
        }

        public String getDriverUsername() { return driverUsername; }
        public String getFareOffer() { return fareOffer; }
        public String getRideId() { return rideId; }
    }
}