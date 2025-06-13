package com.kulsin.gate_keeper.logging;

import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class TraceIdFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String traceId = UUID.randomUUID().toString();

        MDC.put("traceId", traceId);

        // Proceed with the next filter in the chain
        return chain.filter(exchange)
                .doFinally(signalType -> {
                    // Clear MDC to prevent leakage of context after processing the request
                    MDC.clear();
                });
    }

}
