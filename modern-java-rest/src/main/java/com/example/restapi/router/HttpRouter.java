package com.example.restapi.router;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sun.net.httpserver.HttpServer;
import com.example.restapi.handlers.HealthHandler;

public class HttpRouter {
  private static final Logger logger = LoggerFactory.getLogger(HttpRouter.class);
  private final HttpServer server;

  public HttpRouter(HttpServer server) {
    this.server = server;
  }

  public void setupRoutes() {
    server.createContext("/health", new HealthHandler());
    logger.debug("Routes configured");
  }
}
