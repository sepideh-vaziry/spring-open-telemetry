# OpenTelemetry with Spring Boot 4.0 Demo

A minimal Spring Boot demo application that shows basic OpenTelemetry integration (traces, metrics, and logs).

Contents
- Overview
- Requirements
- Build & run
- Docker / Observability stack
- Endpoints
- Logging & OpenTelemetry wiring
- Tests
- Troubleshooting
- License

Overview

This project is a tiny Spring Boot web application with a few endpoints that simulate work and demonstrate how traces, metrics, and logs can be exported to an OpenTelemetry collector/back-end.

Requirements

- Java JDK 25
- Maven (the project includes the Maven wrapper `./mvnw`)
- Docker & Docker Compose (optional, for running the Grafana/OTel demo stack)

Build & run

Build the project with Maven:

```bash
./mvnw -v
./mvnw package -DskipTests
```

Run with the Maven Spring Boot plugin (dev):

```bash
./mvnw spring-boot:run
```

Or run the packaged jar:

```bash
java -jar target/spring-open-telemetry-0.0.1-SNAPSHOT.jar
```

By default the application will start on port 8080.

Docker / Observability stack

The repository contains `compose.yaml` which runs the `grafana/otel-lgtm` image exposing OTLP ports and Grafana UI. By running the project, the Docker compose file will also run.

This image exposes:
- Grafana UI: http://localhost:3000
- OTLP gRPC: 4317
- OTLP HTTP: 4318

Configuration in `src/main/resources/application.yaml` already points the application's OTLP exporters to `http://localhost:4318` (for traces, metrics, and logs):

```yaml
spring:
  otlp:
    metrics:
      export:
        url: http://localhost:4318/v1/metrics
  opentelemetry:
    tracing:
      export:
        otlp:
          endpoint: http://localhost:4318/v1/traces
    logging:
      export:
        otlp:
          endpoint: http://localhost:4318/v1/logs

management:
  tracing:
    sampling:
      probability: 1.0
```

Endpoints

- GET /greet/{name}
  - Returns: "Hello, {name}!"
  - Logs: INFO message containing the name
  - Adds a small simulated delay (50ms)

- GET /slow
  - Returns: "Done!"
  - Simulates ~500ms work

- GET /very-slow
  - Returns: "Done!"
  - Simulates ~2000ms work

Sample curl calls:

```bash
curl http://localhost:8080/greet/Alice
curl http://localhost:8080/slow
curl http://localhost:8080/very-slow
```

Logging & OpenTelemetry wiring

- `logback-spring.xml` configures an `OpenTelemetryAppender` (io.opentelemetry.instrumentation.logback.appender.v1_0.OpenTelemetryAppender) under the name `OTEL` and attaches it to the root logger so logs are exported to the OTLP endpoint.
- `InstallOpenTelemetryAppender` is a small Spring component that installs the OpenTelemetry appender programmatically using the `OpenTelemetry` bean provided by `spring-boot-starter-opentelemetry`.
- Traces and metrics are exported via the Spring Boot OpenTelemetry starter configured in `application.yaml`.

Troubleshooting

- If you don't see traces/logs in the Grafana instance, ensure `compose.yaml` stack is running and that the OTLP HTTP endpoint (`http://localhost:4318`) is reachable from the application.
- Ensure the application's sampling (configured under `management.tracing.sampling.probability`) is set appropriately (1.0 for full sampling in this demo).
- If using a different OpenTelemetry collector, update the OTLP endpoints in `application.yaml` or set the corresponding environment variables when running the app.