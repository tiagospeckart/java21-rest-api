# Java RestAPI

A modern REST API implementation using Java 21 features, including Virtual Threads and Pattern Matching.

## Installing

```bash
mvn clean install
```

## Running

### Using maven

```bash
mvn exec:java -Dexec.mainClass="com.example.restapi.Main"
```

### Using JAR

```bash
java -jar target/modern-java-rest-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Testing the Server

```bash
curl http://localhost:8080/health
```

Should return "ok"
