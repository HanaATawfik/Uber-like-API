package com.uber_like.ride_sharing.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.uber_like.ride_sharing")
public class RideServer {

    public static void main(String[] args) {
        SpringApplication.run(RideServer.class, args);
    }
}