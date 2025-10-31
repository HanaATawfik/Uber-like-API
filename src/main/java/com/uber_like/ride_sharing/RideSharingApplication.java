package com.uber_like.ride_sharing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Ride Sharing Application
 *
 * This is a Spring Boot application that can run:
 * 1. Server mode (default)
 * 2. Client mode (with --client flag)
 */
@SpringBootApplication
public class RideSharingApplication {

	public static void main(String[] args) {
		// Check if running in client mode
		if (args.length > 0 && args[0].equals("--client")) {
			// Run as client (traditional socket client)
			runClient(args);
		} else {
			// Run as Spring Boot server
			SpringApplication.run(RideSharingApplication.class, args);
		}
	}

	/**
	 * Run the application in client mode
	 * Usage: java -jar app.jar --client <server-host> <server-port>
	 */
	private static void runClient(String[] args) {
		System.out.println("Starting in CLIENT mode...");

		if (args.length < 3) {
			System.out.println("Usage: java -jar app.jar --client <server-host> <server-port>");
			System.out.println("Example: java -jar app.jar --client localhost 12345");
			System.exit(1);
		}

		String serverHost = args[1];
		int serverPort;

		try {
			serverPort = Integer.parseInt(args[2]);
		} catch (NumberFormatException e) {
			System.out.println("Error: Port must be a valid number");
			System.exit(1);
			return;
		}

		// Start client (you'll implement this)
		com.uber_like.ride_sharing.client.RideClient.start(serverHost, serverPort);
	}
}