package com.uber_like.ride_sharing.server;

    import com.uber_like.ride_sharing.server.handler.ClientHandler;
    import com.uber_like.ride_sharing.service.AuthService;
    import com.uber_like.ride_sharing.service.RideService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.boot.CommandLineRunner;
    import org.springframework.stereotype.Component;

    import java.io.*;
    import java.net.*;

    @Component
    public class RideServer implements CommandLineRunner {

        @Value("${socket.server.port:12345}")
        private int port;

        @Value("${socket.server.timeout:0}")
        private int timeout;

        @Autowired
        private AuthService authService;

        @Autowired
        private RideService rideService;

        private ServerSocket serverSocket = null;

        @Override
        public void run(String... args) throws Exception {
            System.out.println("==============================================");
            System.out.println("  Ride Sharing Server Starting...");
            System.out.println("==============================================");

            try {
                serverSocket = new ServerSocket(port);
                serverSocket.setSoTimeout(timeout);
                serverSocket.setReuseAddress(true);

                System.out.println("✓ Server initialized on port " + port);
                System.out.println("✓ Waiting for client connections...");
                System.out.println("==============================================\n");

                startServer();

            } catch (IOException e) {
                System.err.println("Error starting server: " + e.getMessage());
                e.printStackTrace();
            }
        }

        private void startServer() {
            while (true) {
                try {
                    System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");
                    Socket client = serverSocket.accept();
                    System.out.println("Just connected to " + client.getRemoteSocketAddress());

                    ClientHandler clientHandler = new ClientHandler(client, authService, rideService);
                    new Thread(clientHandler).start();

                } catch (SocketTimeoutException s) {
                    System.out.println("Socket timed out!");
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }