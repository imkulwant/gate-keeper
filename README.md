# gate-keeper

Ping Service1:
```curl
curl --location 'http://localhost:8080/service1/api/ping'
```

Ping Service2:
```curl
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