package com.uber_like.ride_sharing.client;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class ServerListener implements Runnable {
    private final DataInputStream inFromServer;
    private final BlockingQueue<String> responseQueue;
    private final List<String[]> receivedBids;
    private volatile boolean running = true;

    public ServerListener(DataInputStream inFromServer,
                         BlockingQueue<String> responseQueue,
                         List<String[]> receivedBids) {
        this.inFromServer = inFromServer;
        this.responseQueue = responseQueue;
        this.receivedBids = receivedBids;
    }

    @Override
    public void run() {
        try {
            while (running) {
                String message = inFromServer.readUTF();

                if (message.startsWith("BID:")) {
                    handleBidMessage(message);
                } else {
                    responseQueue.offer(message);
                }
            }
        } catch (IOException e) {
            if (running) {
                System.out.println("Connection lost: " + e.getMessage());
            }
        }
    }

    private void handleBidMessage(String message) {
        String bidData = message.substring(4);
        String[] parts = bidData.split(",");

        if (parts.length == 3) {
            receivedBids.add(parts);
            System.out.println("\n*** New Bid Received! ***");
            System.out.println("Driver: " + parts[0] + ", Fare: $" + parts[1]);
            System.out.print("Enter your choice: ");
        }
    }

    public void stop() {
        running = false;
    }
}