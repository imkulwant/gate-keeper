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
```
redis-cli monitor
```

Check Redis Keys: 
```
redis-cli
> KEYS *
```

Application Status & Metrics:
```bash
curl --location 'http://localhost:8080/actuator/health'
```