package com.uber_like.ride_sharing.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {

    private final ConcurrentHashMap<String, Customer> customerCredentials = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Driver> driverCredentials = new ConcurrentHashMap<>();

    public boolean registerCustomer(String email, String username, String password) {
        if (customerCredentials.containsKey(username)) {
            return false;
        }
        Customer customer = new Customer(email, username, password);
        customerCredentials.put(username, customer);
        return true;
    }

    public boolean registerDriver(String email, String username, String password) {
        if (driverCredentials.containsKey(username)) {
            return false;
        }
        Driver driver = new Driver(email, username, password, "available");
        driverCredentials.put(username, driver);
        return true;
    }

    public Customer loginCustomer(String username, String password) {
        Customer customer = customerCredentials.get(username);
        if (customer != null && customer.getPassword().equals(password)) {
            return customer;
        }
        return null;
    }

    public Driver loginDriver(String username, String password) {
        Driver driver = driverCredentials.get(username);
        if (driver != null && driver.getPassword().equals(password)) {
            return driver;
        }
        return null;
    }

    public Driver getDriver(String username) {
        return driverCredentials.get(username);
    }

    public Customer getCustomer(String username) {
        return customerCredentials.get(username);
    }

    // Inner classes
    public static class Customer {
        private final String username;
        private final String email;
        private final String password;

        public Customer(String email, String username, String password) {
            this.username = username;
            this.email = email;
            this.password = password;
        }

        public String getUsername() { return username; }
        public String getEmail() { return email; }
        public String getPassword() { return password; }
    }

    public static class Driver {
        private final String username;
        private final String email;
        private final String password;
        private String status;

        public Driver(String email, String username, String password, String status) {
            this.username = username;
            this.email = email;
            this.password = password;
            this.status = status;
        }

        public String getUsername() { return username; }
        public String getEmail() { return email; }
        public String getPassword() { return password; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}