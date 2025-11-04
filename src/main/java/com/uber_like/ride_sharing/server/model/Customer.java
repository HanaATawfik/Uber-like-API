package com.uber_like.ride_sharing.model;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class Customer {
    private String username;
    private String email;
    private String password;
}