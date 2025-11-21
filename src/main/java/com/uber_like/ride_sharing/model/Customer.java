package com.uber_like.ride_sharing.model;

import jakarta.persistence.*;

//entity class representing a customer in the ride-sharing application
@Entity   //specifies that this class is an entity and is mapped to a database table
@Table(name = "customers")  //specifies the name of the database table to be used for mapping
public class Customer {

    @Id  //specifies the primary key of an entity
    @GeneratedValue(strategy = GenerationType.IDENTITY) //provides the specification of generation strategies for the values of primary keys
    private Long id;

    @Column(unique = true, nullable = false)  //specifies the details of the column to which a field or property will be mapped
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    // Default constructor required by JPA
    public Customer() {}

    public Customer(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
