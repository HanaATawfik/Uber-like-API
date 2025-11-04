package com.uber_like.ride_sharing.server.model;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class Ride {
    private String rideId;
    private String pickupLocation;
    private String dropLocation;
    private String customerUsername;
    private String driverUsername;
    private String customerFare;
    private String status = "PENDING";
    private List<Bid> bids = new ArrayList<>();
}