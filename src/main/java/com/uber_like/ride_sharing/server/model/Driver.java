package com.uber_like.ride_sharing.model;

import lombok.Data;

@Data
public class Driver {
    private String username;
    private String email;
    private String password;
    private String status = "available";
}