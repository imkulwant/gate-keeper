# gate-keeper

Ping Service1:
```curl
>>>>>>> f0e2ed49e1db0a823b4a94220dc7981de237f237
curl --location 'http://localhost:8080/service1/api/ping'
```

Ping Service2:
```curl
>>>>>>> f0e2ed49e1db0a823b4a94220dc7981de237f237
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