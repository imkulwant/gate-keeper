# API Gateway

- api-gateway is a Spring Cloud Gateway service that fronts backend APIs and provides routing, rate limiting, circuit breaking, and observability.

## What It Does

- Routes traffic to backend services:
  - `/service1/**` -> `apigateway.routes.service1-uri` (default `http://localhost:8081`)
  - `/service2/**` -> `apigateway.routes.service2-uri` (default `http://localhost:8082`)
- Applies a circuit breaker with fallback (`/fallback`).
- Applies Redis-backed request rate limiting on services using client IP as the key.
- Exposes Actuator health/info/metrics endpoints.
- Publishes OpenAPI/Swagger UI.

## Tech Stack

- Java 21
- Spring Boot 3.5.x
- Spring Cloud Gateway (WebFlux)
- Resilience4j
- Redis (reactive)
- Micrometer + OpenTelemetry Zipkin exporter
- Maven

## Local Stub Services (Optional)

Two Flask scripts are included for local route testing:

- `src/main/resources/scripts/test-server-1.py` (port 8081)
- `src/main/resources/scripts/test-server-2.py` (port 8082)

## Smoke Tests

```bash
# Service 1 route
curl --location 'http://localhost:8080/service1/api/ping'

# Service 2 route (rate limited)
curl --location 'http://localhost:8080/service2/api/ping'

# Health
curl --location 'http://localhost:8080/actuator/health' | jq
```

Monitor Redis:

```shell
redis-cli monitor
```

Check Redis Keys:

```shell
redis-cli
> KEYS *
```

Application Status & Metrics:

```bash
curl --location 'http://localhost:8080/actuator/health' | jq
```

```bash
curl 'http://localhost:8080/actuator/health/liveness' | jq
```

```bash
curl 'http://localhost:8080/actuator/health/readiness' | jq
```

```shell
curl --location 'localhost:8080/actuator/metrics'
```

Swagger & OpenAPI:

```shell
curl --location 'localhost:8080/swagger-ui.html'
```

```shell
curl --location 'localhost:8080/api-docs'
```

Install Redis

```bash
brew install redis
```

Start Redis

```bash
brew services start redis
```
