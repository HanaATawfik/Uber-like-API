package com.uber_like.ride_sharing.client;

import com.uber_like.ride_sharing.client.menu.Menu;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class RideClient {
    private static Socket client;
    private static DataOutputStream outToServer;
    private static DataInputStream inFromServer;
    private static BufferedReader userInput;
    private static volatile boolean running = true;
    private static String currentUsername = null;
    private static boolean isCustomer = false;
    private static List<String[]> receivedBids = new ArrayList<>();

    // Queue for synchronous responses
    private static BlockingQueue<String> responseQueue = new LinkedBlockingQueue<>();
    private static ServerListener serverListener;

    public static void start(String serverHost, int serverPort) {
        try {
            System.out.println("Connecting to " + serverHost + " on port " + serverPort);
            client = new Socket(serverHost, serverPort);
            System.out.println("Just connected to " + client.getRemoteSocketAddress());

            outToServer = new DataOutputStream(client.getOutputStream());
            inFromServer = new DataInputStream(client.getInputStream());
            userInput = new BufferedReader(new InputStreamReader(System.in));

            Menu mainMenu = Menu.createMainMenu(userInput);
            int mainChoice = getValidMenuChoice(mainMenu, 1, 2);
            outToServer.writeUTF(String.valueOf(mainChoice));

            if (mainChoice == 1) {
                if (handleCustomer()) {
                    serverListener = new ServerListener(inFromServer, responseQueue, receivedBids);
                    new Thread(serverListener).start();
                    customerMainLoop();
                }
            } else {
                if (handleDriver()) {
                    serverListener = new ServerListener(inFromServer, responseQueue, receivedBids);
                    new Thread(serverListener).start();
                    driverMainLoop();
                }
            }

        } catch (IOException e) {
            System.out.println("Connection error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cleanup();
        }
    }

    private static int getValidMenuChoice(Menu menu, int min, int max) {
        while (true) {
            try {
                int choice = menu.display();
                if (choice >= min && choice <= max) {
                    return choice;
                }
                System.out.println("Error: Please enter a number between " + min + " and " + max);
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number");
                try {
                    userInput.readLine();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } catch (IOException e) {
                System.out.println("Error reading input: " + e.getMessage());
            }
        }
    }

    private static String getNonEmptyInput(String prompt) throws IOException {
        while (true) {
            System.out.print(prompt);
            String input = userInput.readLine();
            if (input != null && !input.trim().isEmpty()) {
                return input.trim();
            }
            System.out.println("Error: Input cannot be empty. Please try again.");
        }
    }

    private static String getValidNumber(String prompt) throws IOException {
        while (true) {
            String input = getNonEmptyInput(prompt);
            try {
                Double.parseDouble(input);
                return input;
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number.");
            }
        }
    }

    private static String readSyncResponse() {
        try {
            return responseQueue.take();
        } catch (InterruptedException e) {
            System.out.println("Interrupted while waiting for response");
            Thread.currentThread().interrupt();
            return null;
        }
    }

    private static void customerMainLoop() {
        try {
            Menu customerMenu = Menu.createCustomerMenu(userInput);
            while (running) {
                int choice = getValidMenuChoice(customerMenu, 1, 4);
                outToServer.writeUTF(String.valueOf(choice));
                outToServer.flush();

                if (choice == 4) {
                    System.out.println("Disconnecting...");
                    running = false;
                    break;
                }

                switch (choice) {
                    case 1:
                        handleRideRequest();
                        break;
                    case 2:
                        handleViewRideStatus();
                        break;
                    case 3:
                        handleAcceptBid();
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println("Connection error: " + e.getMessage());
        }
    }

    private static void driverMainLoop() {
        try {
            Menu driverMenu = Menu.createDriverMenu(userInput);
            while (running) {
                int choice = getValidMenuChoice(driverMenu, 1, 3);
                outToServer.writeUTF(String.valueOf(choice));
                outToServer.flush();

                if (choice == 3) {
                    System.out.println("Disconnecting...");
                    running = false;
                    break;
                }

                switch (choice) {
                    case 1:
                        handleOfferFare();
                        break;
                    case 2:
                        handleUpdateRideStatus();
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println("Connection error: " + e.getMessage());
        }
    }

    private static boolean handleCustomer() throws IOException {
        System.out.println("You chose Customer");
        Menu authMenu = Menu.createAuthMenu(userInput);
        int choice = getValidMenuChoice(authMenu, 1, 2);
        outToServer.writeUTF(String.valueOf(choice));
        outToServer.flush();

        if (choice == 1) {
            return handleSignup(true);
        } else {
            return handleLogin(true);
        }
    }

    private static boolean handleDriver() throws IOException {
        System.out.println("You chose Driver");
        Menu authMenu = Menu.createAuthMenu(userInput);
        int choice = getValidMenuChoice(authMenu, 1, 2);
        outToServer.writeUTF(String.valueOf(choice));
        outToServer.flush();

        if (choice == 1) {
            return handleSignup(false);
        } else {
            return handleLogin(false);
        }
    }

    private static boolean handleSignup(boolean isCustomerFlag) throws IOException {
        System.out.println("You chose to Sign up");

        String email = getNonEmptyInput("Please enter your email: ");
        String username = getNonEmptyInput("Please enter your username: ");
        String password = getNonEmptyInput("Please enter your password: ");

        outToServer.writeUTF(email);
        outToServer.writeUTF(username);
        outToServer.writeUTF(password);
        outToServer.flush();

        String serverResponse = inFromServer.readUTF();
        System.out.println("Server response: " + serverResponse);

        if (serverResponse.startsWith("SUCCESS")) {
            currentUsername = username;
            isCustomer = isCustomerFlag;
            return true;
        }
        return false;
    }

    private static boolean handleLogin(boolean isCustomerFlag) throws IOException {
        System.out.println("You chose to Log in");

        String username = getNonEmptyInput("Please enter your username: ");
        String password = getNonEmptyInput("Please enter your password: ");

        outToServer.writeUTF(username);
        outToServer.writeUTF(password);
        outToServer.flush();

        String serverResponse = inFromServer.readUTF();
        System.out.println("Server response: " + serverResponse);

        if (serverResponse.startsWith("SUCCESS")) {
            currentUsername = username;
            isCustomer = isCustomerFlag;
            return true;
        }
        return false;
    }

    private static void handleRideRequest() {
        try {
            String pickupLocation = getNonEmptyInput("Enter pickup location: ");
            String dropLocation = getNonEmptyInput("Enter drop-off location: ");
            String fare = getValidNumber("Enter your suggested fare (in dollars): ");

            outToServer.writeUTF(pickupLocation);
            outToServer.writeUTF(dropLocation);
            outToServer.writeUTF(fare);
            outToServer.flush();

            String response = readSyncResponse();
            if (response != null) {
                System.out.println(response);
            }

        } catch (IOException e) {
            System.out.println("Error requesting ride: " + e.getMessage());
        }
    }

    private static void handleViewRideStatus() {
        try {
            String statusMessage = readSyncResponse();
            if (statusMessage != null) {
                System.out.println(statusMessage);
            }
        } catch (Exception e) {
            System.out.println("Error viewing ride status: " + e.getMessage());
        }
    }

    private static void handleAcceptBid() {
        try {
            if (receivedBids.isEmpty()) {
                System.out.println("No bids received yet.");
                return;
            }

            System.out.println("\n=== Received Bids ===");
            for (int i = 0; i < receivedBids.size(); i++) {
                String[] bid = receivedBids.get(i);
                System.out.println((i + 1) + ". Driver: " + bid[0] + ", Fare: $" + bid[1] + ", Ride ID: " + bid[2]);
            }

            String choice = getNonEmptyInput("Enter the number of the bid to accept (or 0 to cancel): ");
            int bidIndex;

            try {
                bidIndex = Integer.parseInt(choice) - 1;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                return;
            }

            if (bidIndex == -1) {
                System.out.println("Bid acceptance cancelled.");
                return;
            }

            if (bidIndex < 0 || bidIndex >= receivedBids.size()) {
                System.out.println("Invalid choice.");
                return;
            }

            String[] selectedBid = receivedBids.get(bidIndex);
            String driverUsername = selectedBid[0];

            outToServer.writeUTF(driverUsername);
            outToServer.flush();

            String response = readSyncResponse();
            if (response != null) {
                System.out.println(response);

                if (response.startsWith("SUCCESS")) {
                    receivedBids.clear();
                }
            }

        } catch (IOException e) {
            System.out.println("Error accepting bid: " + e.getMessage());
        }
    }

    private static void handleOfferFare() {
        try {
            System.out.println("Checking your availability status...");

            String availabilityResponse = readSyncResponse();
            if (availabilityResponse == null) return;

            System.out.println(availabilityResponse);

            if (availabilityResponse.startsWith("FAILURE:")) {
                return;
            }

            String rideRequestsMessage = readSyncResponse();
            if (rideRequestsMessage == null) return;

            if (rideRequestsMessage.startsWith("INFO: No ride requests")) {
                System.out.println(rideRequestsMessage);
                return;
            }

            if (rideRequestsMessage.startsWith("RIDE_REQUESTS:")) {
                String ridesData = rideRequestsMessage.substring("RIDE_REQUESTS:".length());
                String[] rides = ridesData.split("\\|");

                System.out.println("\n=== Available Ride Requests ===");
                for (int i = 0; i < rides.length; i++) {
                    System.out.println((i + 1) + ". " + rides[i]);
                }

                String choice = getNonEmptyInput("Enter the number of the ride to bid on (or 0 to cancel): ");
                outToServer.writeUTF(choice);
                outToServer.flush();

                String selectedRideResponse = readSyncResponse();
                if (selectedRideResponse == null) return;

                if (selectedRideResponse.startsWith("INFO:") || selectedRideResponse.startsWith("FAILURE:")) {
                    System.out.println(selectedRideResponse);
                    return;
                }

                if (selectedRideResponse.startsWith("Selected ride:")) {
                    System.out.println(selectedRideResponse);

                    String fareOffer = getValidNumber("Enter your fare offer (in dollars): ");
                    outToServer.writeUTF(fareOffer);
                    outToServer.flush();

                    String bidConfirmation = readSyncResponse();
                    if (bidConfirmation != null) {
                        System.out.println(bidConfirmation);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error processing ride bids: " + e.getMessage());
        }
    }

    private static void handleUpdateRideStatus() {
        try {
            String statusMessage = readSyncResponse();
            if (statusMessage == null) return;

            if (statusMessage.startsWith("INFO: You have no active ride")) {
                System.out.println(statusMessage);
                return;
            }

            if (statusMessage.startsWith("RIDE_STATUS_MENU:")) {
                String currentStatus = statusMessage.substring("RIDE_STATUS_MENU:".length());
                System.out.println(currentStatus);

                Menu statusMenu = Menu.createRideStatusMenu(userInput);
                int choice = getValidMenuChoice(statusMenu, 1, 3);
                outToServer.writeUTF(String.valueOf(choice));
                outToServer.flush();

                String response = readSyncResponse();
                if (response != null) {
                    System.out.println(response);
                }
            }
        } catch (IOException e) {
            System.out.println("Error updating ride status: " + e.getMessage());
        }
    }

    private static void cleanup() {
        running = false;
        try {
            Thread.sleep(100);

            if (userInput != null) userInput.close();
            if (outToServer != null) outToServer.close();
            if (inFromServer != null) inFromServer.close();
            if (client != null) client.close();

            System.out.println("Connection closed successfully.");
        } catch (IOException e) {
            System.out.println("Error during cleanup: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void setRunning(boolean value) {
        running = value;
    }
}