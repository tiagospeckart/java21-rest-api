# Exercise 03

Implement a custom router using pattern matching:

```java
// Exercise 3: Implement this interface
public interface Router {
    void addRoute(String path, String method, HttpHandler handler);
    Optional<HttpHandler> findHandler(String path, String method);
}
```
