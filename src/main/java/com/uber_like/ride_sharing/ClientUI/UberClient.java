package com.uber_like.ride_sharing.ClientUI;

import com.uber_like.ride_sharing.ClientUI.menu.MenuHandler;
import com.uber_like.ride_sharing.ClientUI.menu.service.ApiService;
import java.io.*;

public class UberClient {

    // ✅ CLEANEST MAIN - Only initializes and starts
    public static void main(String[] args) {
        try {
            // 1. Create the application
            UberClientApp app = new UberClientApp();

            // 2. Run it
            app.start();

        } catch (Exception e) {
            System.out.println("Application error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

// ✅ ALL LOGIC MOVED HERE
class UberClientApp {
    private static final String API_BASE_URL = "http://localhost:8080/api";

    private BufferedReader userInput;
    private ApiService apiService;
    private MenuHandler menuHandler;
    private String currentUsername;
    private boolean isCustomer;
    private boolean running;

    public UberClientApp() {
        // Initialize resources
        this.userInput = new BufferedReader(new InputStreamReader(System.in));
        this.apiService = new ApiService(API_BASE_URL);
        this.running = true;
    }

    public void start() throws IOException {
        try {
            showWelcome();

            // Step 1: Choose role (Customer or Driver)
            isCustomer = selectRole();

            // Step 2: Authenticate
            if (!authenticate()) {
                System.out.println("Authentication failed. Exiting...");
                return;
            }

            // Step 3: Initialize menu handler
            menuHandler = new MenuHandler(apiService, userInput);
            menuHandler.setUsername(currentUsername);

            // Step 4: Run main loop
            runMainLoop();

        }
        finally {
            cleanup();
        }
    }

    private void showWelcome() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║    Welcome to Uber-like Application   ║");
        System.out.println("╚════════════════════════════════════════╝");
    }

    private boolean selectRole() throws IOException {
        System.out.println("\n=== Select Your Role ===");
        System.out.println("1. Customer");
        System.out.println("2. Driver");
        System.out.print("Choose: ");

        int choice = Integer.parseInt(userInput.readLine());
        return choice == 1; // true = customer, false = driver
    }

    private boolean authenticate() throws IOException {
        System.out.println("\n=== Authentication ===");
        System.out.println("1. Sign up");
        System.out.println("2. Log in");
        System.out.print("Choose: ");

        int authChoice = Integer.parseInt(userInput.readLine());

        if (authChoice == 1) {
            return handleSignup();
        } else {
            return handleLogin();
        }
    }

    private boolean handleSignup() throws IOException {
        System.out.println("\n=== Sign Up ===");

        System.out.print("Email: ");
        String email = userInput.readLine().trim();

        System.out.print("Username: ");
        String username = userInput.readLine().trim();

        System.out.print("Password: ");
        String password = userInput.readLine().trim();

        try {
            java.util.Map<String, Object> response;

            if (isCustomer) {
                response = apiService.customerSignup(email, username, password);
            } else {
                response = apiService.driverSignup(email, username, password);
            }

            System.out.println(response.get("message"));

            if ("SUCCESS".equals(response.get("status"))) {
                currentUsername = username;
                return true;
            }
            return false;

        } catch (Exception e) {
            System.out.println("Signup error: " + e.getMessage());
            return false;
        }
    }

    private boolean handleLogin() throws IOException {
        System.out.println("\n=== Log In ===");

        System.out.print("Username: ");
        String username = userInput.readLine().trim();

        System.out.print("Password: ");
        String password = userInput.readLine().trim();

        try {
            java.util.Map<String, Object> response;

            if (isCustomer) {
                response = apiService.customerLogin(username, password);
            } else {
                response = apiService.driverLogin(username, password);
            }

            System.out.println(response.get("message"));

            if ("SUCCESS".equals(response.get("status"))) {
                currentUsername = username;
                return true;
            }
            return false;

        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
            return false;
        }
    }

    private void runMainLoop() throws IOException {
        while (running) {
            if (isCustomer) {
                showCustomerMenu();
            } else {
                showDriverMenu();
            }
        }
    }

    private void showCustomerMenu() throws IOException {
        System.out.println("\n=== Customer Menu ===");
        System.out.println("1. Request a Ride");
        System.out.println("2. View Ride Status");
        System.out.println("3. Accept Driver Bid");
        System.out.println("4. Exit");
        System.out.print("Choose: ");

        int choice = Integer.parseInt(userInput.readLine());

        menuHandler.handleCustomerMenu(choice);

        if (choice == 4) {
            running = false;
        }
    }

    private void showDriverMenu() throws IOException {
        System.out.println("\n=== Driver Menu ===");
        System.out.println("1. Offer Ride Fare");
        System.out.println("2. Update Ride Status");
        System.out.println("3. Exit");
        System.out.print("Choose: ");

        int choice = Integer.parseInt(userInput.readLine());

        menuHandler.handleDriverMenu(choice);

        if (choice == 3) {
            running = false;
        }
    }

    private void cleanup() {
        try {
            if (userInput != null) {
                userInput.close();
            }
        } catch (IOException e) {
            System.out.println("Error closing resources: " + e.getMessage());
        }

        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║          Thank you for using          ║");
        System.out.println("║        Uber-like Application!         ║");
        System.out.println("╚════════════════════════════════════════╝");
    }
}