# Study Note 1: Modern Java HTTP Server

## HttpServer Basics

- Package: com.sun.net.httpserver
- No framework dependencies
- Built-in virtual thread support
- Lightweight and fast

## Key Components

1. HttpServer
2. HttpHandler
3. HttpExchange
4. HttpContext

## Example Setup

```java
var server = HttpServer.create(new InetSocketAddress(8080), 0);
server.createContext("/api/users", new UserHandler());
server.setExecutor(null); // Use virtual threads
server.start();
```
