package com.uber_like.ride_sharing.server.model;

import lombok.Data;

@Data
public class Bid {
    private String driverUsername;
    private String fareOffer;
    private String rideId;
}