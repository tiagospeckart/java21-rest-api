package com.example.restapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.restapi.server.ApplicationServer;
import com.example.restapi.config.ServerConfig;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            ServerConfig config = new ServerConfig(8080);
            ApplicationServer server = new ApplicationServer(config);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Shutting down server...");
                server.stop();
            }));

            server.start();
            logger.info("Server started on port {}", config.port());
            Thread.currentThread().join();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Server was interrupted", e);
            System.exit(1);
        } catch (Exception e) {
            logger.error("Server failed to start: {}", e.getMessage(), e);
            System.exit(1);
        }
    }
}
