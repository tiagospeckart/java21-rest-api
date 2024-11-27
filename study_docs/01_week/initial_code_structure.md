# Code structure

```txt
├── pom.xml
├── src
│   ├── main
│   │   └── java
│   │       └── com
│   │           └── example
│   │               └── restapi
│   │                   ├── Main.java
│   │                   ├── server
│   │                   │   ├── HttpRouter.java
│   │                   │   └── Server.java
│   │                   ├── handlers
│   │                   │   └── UserHandler.java
│   │                   ├── models
│   │                   │   └── User.java
│   │                   └── repositories
│   │                       └── UserRepository.java
│   └── test
│       └── java
│           └── com
│               └── example
│                   └── restapi
│                       ├── server
│                       │   └── ServerTest.java
│                       ├── handlers
│                       │   └── UserHandlerTest.java
│                       └── models
│                           └── UserTest.java
```

## pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>modern-java-rest</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <junit.version>5.10.0</junit.version>
        <jackson.version>2.15.2</jackson.version>
    </properties>

    <dependencies>
        <!-- Testing -->
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-api</artifactId>
            <version>${junit.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-engine</artifactId>
            <version>${junit.version}</version>
            <scope>test</scope>
        </dependency>

        <!-- JSON Processing -->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>${jackson.version}</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>21</source>
                    <target>21</target>
                    <compilerArgs>
                        <arg>--enable-preview</arg>
                    </compilerArgs>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.1.2</version>
                <configuration>
                    <argLine>--enable-preview</argLine>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

## Server test implementation

```java
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
```

## Server implementation

```java
package com.example.restapi.server;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.util.concurrent.Executors;

public class Server {
    private final HttpServer server;

    public Server(int port) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(port), 0);

        // Configure server to use virtual threads
        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());

        // Add basic health check endpoint
        server.createContext("/health", exchange -> {
            var response = "OK";
            exchange.sendResponseHeaders(200, response.length());
            try (var os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        });
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(0);
    }
}
```
