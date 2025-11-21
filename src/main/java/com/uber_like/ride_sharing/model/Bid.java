package com.uber_like.ride_sharing.model;

import jakarta.persistence.*;

@Entity
@Table(name = "bids")
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String driverUsername;

    @Column(nullable = false)
    private String fareOffer;

    @Column(nullable = false)
    private String rideId;

    // Default constructor required by JPA
    public Bid() {}

    public Bid(String driverUsername, String fareOffer, String rideId) {
        this.driverUsername = driverUsername;
        this.fareOffer = fareOffer;
        this.rideId = rideId;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDriverUsername() { return driverUsername; }
    public void setDriverUsername(String driverUsername) { this.driverUsername = driverUsername; }

    public String getFareOffer() { return fareOffer; }
    public void setFareOffer(String fareOffer) { this.fareOffer = fareOffer; }

    public String getRideId() { return rideId; }
    public void setRideId(String rideId) { this.rideId = rideId; }
}