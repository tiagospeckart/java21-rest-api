package com.example.restapi.config;

public record ServerConfig(int port, int backlog, String contextPath) {
  // Canonical constructor validation
  public ServerConfig {
    if (port <= 0) {
      throw new IllegalArgumentException("Port must be positive");
    }
    if (backlog < 0) {
      throw new IllegalArgumentException("Backlog cannot be negative");
    }
    if (contextPath == null || contextPath.isEmpty()) {
      throw new IllegalArgumentException("Context path cannot be null or empty");
    }
  }

  // Custom constructor for convenience
  public ServerConfig(int port) {
    this(port, 0, "/api");
  }
}
