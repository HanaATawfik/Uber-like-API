package com.uber_like.ride_sharing.ClientUI;

import com.uber_like.ride_sharing.ClientUI.menu.MenuHandler;
import com.uber_like.ride_sharing.ClientUI.menu.service.ApiService;
import jdk.internal.io.JdkConsoleImpl;

import java.io.*;
import java.util.Scanner;
import java.util.List;
import java.util.Map;

public class UberClient {
    private static Scanner scanner;
    private static String currentUsername = null;
    private static boolean isCustomer = false;
    private static volatile boolean running = true;
    private static ApiService apiService;
    private static MenuHandler menuHandler;

    static class Menu {
        private final String title;
        private final List<String> options;
        private final BufferedReader input;

        public Menu(String title, List<String> options, BufferedReader input) {
            this.title = title;
            this.options = options;
            this.input = input;
        }

        public int display() throws IOException {
            System.out.println("\n=== " + title + " ===");
            for (int i = 0; i < options.size(); i++) {
                System.out.println((i + 1) + ". " + options.get(i));
            }
            System.out.print("Please enter your choice: ");
            return Integer.parseInt(input.readLine());
        }

        public static Menu createMainMenu(BufferedReader input) {
            return new Menu("Main Menu",
                    List.of("Customer", "Driver"), input);
        }

        public static Menu createAuthMenu(BufferedReader input) {
            return new Menu("Authentication",
                    List.of("Sign up", "Log in"), input);
        }

        public static Menu createCustomerMenu(BufferedReader input) {
            return new Menu("Customer Menu",
                    List.of("Request a Ride", "View Ride Status", "Accept Driver Bid", "Exit"), input);
        }

        public static Menu createDriverMenu(BufferedReader input) {
            return new Menu("Driver Menu",
                    List.of("Offer Ride Fare", "Update Ride Status", "Exit"), input);
        }

        public static Menu createRideStatusMenu(BufferedReader input) {
            return new Menu("Update Ride Status",
                    List.of("Start Ride", "Complete Ride", "Cancel"), input);
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
                JdkConsoleImpl userInput = null;
                userInput.readLine(); // Clear buffer
            } catch (IOException e) {
                System.out.println("Error reading input: " + e.getMessage());
            }
        }
    }

    private static String getNonEmptyInput(String prompt) throws IOException {
        while (true) {
            System.out.print(prompt);
            JdkConsoleImpl userInput=null;
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

    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        apiService = new ApiService("http://localhost:8080/api");
       menuHandler = new MenuHandler();
        try (BufferedReader input = new BufferedReader(new InputStreamReader(System.in))) {
            while (running) {
                Menu mainMenu = Menu.createMainMenu(input);
                int mainChoice = mainMenu.display();

                if (mainChoice == 1) {
                    isCustomer = true;
                } else if (mainChoice == 2) {
                    isCustomer = false;
                } else {
                    System.out.println("Invalid choice. Please try again.");
                    continue;
                }

                Menu authMenu = Menu.createAuthMenu(input);
                int authChoice = authMenu.display();

                if (authChoice == 1) {
                    System.out.print("Enter username: ");
                    String username = input.readLine();
                    System.out.print("Enter password: ");
                    String password = input.readLine();
                    boolean success = apiService.signUp(username, password, isCustomer);
                    if (success) {
                        System.out.println("Sign up successful. Please log in.");
                    } else {
                        System.out.println("Sign up failed. Try again.");
                        continue;
                    }
                }

                System.out.print("Enter username: ");
                String username = input.readLine();
                System.out.print("Enter password: ");
                String password = input.readLine();
                boolean loggedIn = apiService.logIn(username, password, isCustomer);
                if (!loggedIn) {
                    System.out.println("Login failed. Try again.");
                    continue;
                }
                currentUsername = username;

                if (isCustomer) {
                    handleCustomerMenu(input);
                } else {
                    handleDriverMenu(input);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}