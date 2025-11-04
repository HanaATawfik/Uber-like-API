package com.uber_like.ride_sharing.server.handler;

import com.uber_like.ride_sharing.service.AuthService;
import com.uber_like.ride_sharing.service.RideService;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private final AuthService authService;
    private final RideService rideService;
    private DataInputStream in;
    private DataOutputStream out;
    private volatile boolean running = true;

    public ClientHandler(Socket socket, AuthService authService, RideService rideService) {
        this.clientSocket = socket;
        this.authService = authService;
        this.rideService = rideService;
    }

    @Override
    public void run() {
        try {
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());

            // Your client handling logic here

        } catch (IOException e) {
            System.out.println("Client disconnected: " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    private void cleanup() {
        running = false;
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null) clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}