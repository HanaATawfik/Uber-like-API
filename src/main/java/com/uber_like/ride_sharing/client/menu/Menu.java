package com.uber_like.ride_sharing.client.menu;

import java.io.BufferedReader;
import java.io.IOException;

public class Menu {
    private final BufferedReader reader;
    private final String title;
    private final String[] options;

    private Menu(BufferedReader reader, String title, String[] options) {
        this.reader = reader;
        this.title = title;
        this.options = options;
    }

    public static Menu createMainMenu(BufferedReader reader) {
        return new Menu(reader, "Main Menu", new String[]{
            "1. Customer",
            "2. Driver"
        });
    }

    public static Menu createAuthMenu(BufferedReader reader) {
        return new Menu(reader, "Authentication", new String[]{
            "1. Sign up",
            "2. Log in"
        });
    }

    public static Menu createCustomerMenu(BufferedReader reader) {
        return new Menu(reader, "Customer Menu", new String[]{
            "1. Request a Ride",
            "2. View Ride Status",
            "3. Accept Bid",
            "4. Disconnect"
        });
    }

    public static Menu createDriverMenu(BufferedReader reader) {
        return new Menu(reader, "Driver Menu", new String[]{
            "1. Offer Fare",
            "2. Update Ride Status",
            "3. Disconnect"
        });
    }

    public static Menu createRideStatusMenu(BufferedReader reader) {
        return new Menu(reader, "Update Ride Status", new String[]{
            "1. Mark as Picked Up",
            "2. Mark as Completed",
            "3. Cancel"
        });
    }

    public int display() throws IOException {
        System.out.println("\n=== " + title + " ===");
        for (String option : options) {
            System.out.println(option);
        }
        System.out.print("Enter your choice: ");
        return Integer.parseInt(reader.readLine().trim());
    }
}