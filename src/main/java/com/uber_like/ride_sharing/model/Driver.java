package com.uber_like.ride_sharing.model;

import jakarta.persistence.*;

@Entity
@Table(name = "drivers")
public class Driver {

    @Id  //specifies the primary key of an entity
    @GeneratedValue(strategy = GenerationType.IDENTITY) //provides the specification of generation strategies for the values of primary keys
    private Long id;

    @Column(unique = true, nullable = false)
    private  String username;

    @Column(unique = true, nullable = false)
    private  String email;

    @Column(unique = true, nullable = false)
    private  String password;
    @Column
    private String status = "available";

    public Driver() {
    }

    public Driver(String email, String username, String password, String status) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.status = status;
    }

    public Long getId() { return id; }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() { return username; }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() { return password; }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

}
