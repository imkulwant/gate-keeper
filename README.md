# gate-keeper

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

Install Redis
```bash
brew install redis
```

Start Redis
```bash
brew services start redis
```