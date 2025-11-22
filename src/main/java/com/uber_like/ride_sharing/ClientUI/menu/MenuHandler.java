//CLIENT-SIDE Service (HTTP Communication)
package com.uber_like.ride_sharing.ClientUI.menu;

import com.uber_like.ride_sharing.ClientUI.menu.service.ApiService;

import java.io.BufferedReader;

public class MenuHandler {
    private BufferedReader userInput;
    private ApiService apiService;

    public MenuHandler(ApiService apiService, BufferedReader userInput) {
        this.apiService = apiService;
        this.userInput = userInput;
    }
}