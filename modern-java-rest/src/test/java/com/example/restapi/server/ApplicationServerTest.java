package com.example.restapi.server;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.restapi.config.ServerConfig;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationServerTest {
  private static final int TEST_PORT = 8080;
  private static final int CONCURRENT_PORT = 8081;
  private static final Duration TIMEOUT = Duration.ofSeconds(5);
  private ApplicationServer server;
  private HttpClient client;

  @BeforeEach
  void setUp() {
    client = HttpClient.newBuilder()
        .connectTimeout(TIMEOUT)
        .build();
  }

  @AfterEach
  void tearDown() {
    if (server != null) {
      server.stop();
    }
  }

  @Test
  void serverShouldStartAndRespond() throws Exception {
    // Arrange
    ServerConfig config = new ServerConfig(TEST_PORT);
    server = new ApplicationServer(config);

    // Act
    server.start();
    HttpResponse<String> response = sendRequest(TEST_PORT);

    // Assert
    assertEquals(200, response.statusCode());
    assertEquals("OK", response.body());
  }

  @Test
  void serverShouldHandleConcurrentRequests() throws Exception {
    // Arrange
    ServerConfig config = new ServerConfig(CONCURRENT_PORT);
    server = new ApplicationServer(config);
    server.start();

    // Act
    HttpResponse<String>[] responses = sendConcurrentRequests(10);

    // Assert
    for (HttpResponse<String> response : responses) {
      assertEquals(200, response.statusCode());
      assertEquals("OK", response.body());
    }
  }

  private HttpResponse<String> sendRequest(int port) throws Exception {
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(String.format("http://localhost:%d/health", port)))
        .GET()
        .build();

    return client.send(request, HttpResponse.BodyHandlers.ofString());
  }

  private HttpResponse<String>[] sendConcurrentRequests(int count) throws Exception {
    @SuppressWarnings("unchecked")
    HttpResponse<String>[] responses = new HttpResponse[count];

    Thread[] threads = new Thread[count];
    for (int i = 0; i < count; i++) {
      final int index = i;
      threads[i] = Thread.startVirtualThread(() -> {
        try {
          responses[index] = sendRequest(CONCURRENT_PORT);
        } catch (Exception e) {
          fail("Failed to send concurrent request: " + e.getMessage());
        }
      });
    }

    // Wait for all threads to complete
    for (Thread thread : threads) {
      thread.join();
    }

    return responses;
  }
}
