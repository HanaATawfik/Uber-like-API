package com.uber_like.ride_sharing.model;

import jakarta.persistence.*;

@Entity
@Table(name = "rides")
public class Ride {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private  String rideId;
   @Column(nullable = false)
    private  String pickupLocation;
    @Column(nullable = false)
    private  String dropLocation;

    @Column(nullable = false)
    private  String customerUsername;

    @Column(nullable = false)
    private String driverUsername;

    @Column(nullable = false)

    private  String customerFare;
    @Column(nullable = false)
    private String status = "PENDING";
   // private final <Bid> bids = Collections.synchronizedList(new ArrayList<Bid>());

    public Ride() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRideId() { return rideId; }
    public void setRideId(String rideId) { this.rideId = rideId; }
    public String getPickupLocation() { return pickupLocation; }
    public void setPickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; }
    public String getDropLocation() { return dropLocation; }
    public void setDropLocation(String dropLocation) { this.dropLocation = dropLocation; }
    public String getCustomerUsername() { return customerUsername; }
    public void setCustomerUsername(String customerUsername) { this.customerUsername = customerUsername; }
    public String getDriverUsername() { return driverUsername; }

    public void setDriverUsername(String driverUsername) { this.driverUsername = driverUsername; }
    public String getCustomerFare() { return customerFare; }
    public void setCustomerFare(String customerFare) { this.customerFare = customerFare; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
  //  public List<Bid> getBids() { return bids; }

  /*  public void addBid(Bid bid) {
        bids.add(bid);
    }*/

}
