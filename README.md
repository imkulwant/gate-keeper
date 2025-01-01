# gate-keeper

- An API Gateway Service that acts as a central entry point for all client requests to the backend microservices.
- **Routing:** Directs incoming requests to the appropriate backend services based on predefined routes.
- **Rate Limiting:** Limits the number of requests a client can make in a given time period to prevent abuse and ensure fair usage.
- **Circuit Breaker Pattern:** Utilizes the circuit breaker pattern to prevent cascading failures in the system by temporarily blocking requests to failing services.
- **Monitoring and Metrics:** Exposes metrics through actuator endpoints for easy access and visualization.
- **API Documentation:** Accessible via Swagger UI for easy exploration of available endpoints.

Ping Service1:
```bash
curl --location 'http://localhost:8080/service1/api/ping'
```

Ping Service2:
```bash
curl --location 'http://localhost:8080/service2/api/ping'
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