package com.example.restapi.handlers;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class HealthHandler implements HttpHandler {
  private static final Logger logger = LoggerFactory.getLogger(HealthHandler.class);
  private static final String RESPONSE = "OK";

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    String clientAddress = exchange.getRemoteAddress().toString();
    logger.debug("Processing health check for client: {}", clientAddress);

    try (var os = exchange.getResponseBody()) {
      sendHealthResponse(exchange, os);
    } catch (IOException e) {
      handleHealthCheckError(clientAddress, e);
    }
  }

  private void sendHealthResponse(HttpExchange exchange, OutputStream os) throws IOException {
    byte[] responseBytes = RESPONSE.getBytes(StandardCharsets.UTF_8);
    exchange.sendResponseHeaders(200, responseBytes.length);
    os.write(responseBytes);
    logger.debug("Health check successful");
  }

  private void handleHealthCheckError(String clientAddress, IOException e) throws IOException {
    String errorMessage = String.format("Health check failed for client: %s", clientAddress);
    logger.error(errorMessage, e);

    throw new IOException(errorMessage, e);
  }
}
