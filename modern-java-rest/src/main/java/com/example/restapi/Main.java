package com.example.restapi;

import com.example.restapi.server.Server;

public class Main {
    public static void main(String[] args) {
        try {
            // Create and start server on port 8080
            Server server = new Server(8080);
            server.start();

            System.out.println("Server started on port 8080");
            System.out.println("Visit http://localhost:8080/health to check server status");

            // Keep the main thread alive
            Thread.currentThread().join();

        } catch (Exception e) {
            System.err.println("Server failed to start: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
