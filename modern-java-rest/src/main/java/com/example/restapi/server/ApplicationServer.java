package com.example.restapi.server;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.restapi.config.ServerConfig;
import com.example.restapi.handlers.HealthHandler;
import com.example.restapi.router.HttpRouter;

public class ApplicationServer {
  private static final Logger logger = LoggerFactory.getLogger(ApplicationServer.class);
  private final HttpServer server;

  public ApplicationServer(ServerConfig config) throws IOException {
    this.server = HttpServer.create(
        new InetSocketAddress(config.port()),
        0);
    configureServer();
  }

  private void configureServer() {
    server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
    server.createContext("/health", new HealthHandler());
    logger.debug("Server configured with virtual threads");
  }

  public void start() {
    server.start();
    logger.info("Server is running");
  }

  public void stop() {
    server.stop(0);
    logger.info("Server stopped");
  }
}