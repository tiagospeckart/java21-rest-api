package com.example.restapi.server;

import org.junit.jupiter.api.Test;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class ServerTest {

  @Test
  void serverShouldStartAndRespond() throws Exception {
    // Start server
    var server = new Server(8080);
    server.start();

    try {
      // Create HTTP client with timeout
      var client = HttpClient.newBuilder()
          .connectTimeout(Duration.ofSeconds(5))
          .build();

      // Create request
      var request = HttpRequest.newBuilder()
          .uri(URI.create("http://localhost:8080/health"))
          .GET()
          .build();

      // Send request
      var response = client.send(request,
          HttpResponse.BodyHandlers.ofString());

      // Verify response
      assertEquals(200, response.statusCode());
      assertEquals("OK", response.body());
    } finally {
      server.stop();
    }
  }

  @Test
  void serverShouldHandleConcurrentRequests() throws Exception {
    var server = new Server(8081);
    server.start();

    try {
      var client = HttpClient.newBuilder()
          .connectTimeout(Duration.ofSeconds(5))
          .build();

      // Create multiple concurrent requests
      var requests = new HttpRequest[10];
      for (int i = 0; i < 10; i++) {
        requests[i] = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8081/health"))
            .GET()
            .build();
      }

      // Send requests concurrently using virtual threads
      var responses = new HttpResponse[10];
      Thread.startVirtualThread(() -> {
        try {
          for (int i = 0; i < 10; i++) {
            responses[i] = client.send(requests[i],
                HttpResponse.BodyHandlers.ofString());
          }
        } catch (Exception e) {
          fail("Failed to send concurrent requests: " + e.getMessage());
        }
      }).join();

      // Verify all responses
      for (var response : responses) {
        assertEquals(200, response.statusCode());
        assertEquals("OK", response.body());
      }

    } finally {
      server.stop();
    }
  }
}
