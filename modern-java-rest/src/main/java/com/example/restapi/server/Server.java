package com.example.restapi.server;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.util.concurrent.Executors;

public class Server {
  private final HttpServer httpServer;

  public Server(int port) throws IOException {
    this.httpServer = HttpServer.create(new InetSocketAddress(port), 0);

    // Configure server to use virtual threads
    httpServer.setExecutor(Executors.newVirtualThreadPerTaskExecutor());

    // Add basic health check endpoint
    httpServer.createContext("/health", exchange -> {
      var response = "OK";
      exchange.sendResponseHeaders(200, response.length());
      try (var os = exchange.getResponseBody()) {
        os.write(response.getBytes());
      }
    });
  }

  public void start() {
    httpServer.start();
  }

  public void stop() {
    httpServer.stop(0);
  }
}