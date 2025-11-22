package com.uber_like.ride_sharing.ClientUI.menu.service;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

public class ApiService {
    private final String baseUrl;
    private final RestTemplate restTemplate;
    private String authToken;

    public ApiService(String baseUrl) {
        this.baseUrl = baseUrl;
        this.restTemplate = new RestTemplate();
    }

    // Rest of your methods...
}