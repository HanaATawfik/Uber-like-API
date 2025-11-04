package com.uber_like.ride_sharing;

import com.uber_like.ride_sharing.client.RideClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Ride Sharing Application
 *
 * This is a Spring Boot application that can run:
 * 1. Server mode (default) - Run the socket server
 * 2. Client mode (with --client flag) - Connect to the server
 */
@SpringBootApplication
public class RideSharingApplication {

	public static void main(String[] args) {
		// Check if running in client mode
		if (args.length > 0 && args[0].equals("--client")) {
			runClient(args);
		} else {
			// Run as Spring Boot server
			System.out.println("╔════════════════════════════════════════╗");
			System.out.println("║   RIDE SHARING SERVER - STARTING...    ║");
			System.out.println("╚════════════════════════════════════════╝");
			SpringApplication.run(RideSharingApplication.class, args);
		}
	}

	/**
	 * Run the application in client mode
	 * Usage: java -jar app.jar --client <server-host> <server-port>
	 */
	private static void runClient(String[] args) {
		System.out.println("╔════════════════════════════════════════╗");
		System.out.println("║   RIDE SHARING CLIENT - STARTING...    ║");
		System.out.println("╚════════════════════════════════════════╝\n");

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

		// Start client
		RideClient.start(serverHost, serverPort);
	}
}