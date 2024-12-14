package com.kulsin.gate_keeper.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class LoggingFilter implements GlobalFilter {

    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);

    private static final String REQUEST_LOG_TEMPLATE = """
            === Incoming Request ===
            Method: {}
            URI: {}
            Headers: {}
            Body: {}
            ========================""";

    private static final String RESPONSE_LOG_TEMPLATE = """
            === Outgoing Response ===
            Status: {}
            Headers: {}
            Body: {}
            ========================""";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        log.info(REQUEST_LOG_TEMPLATE, exchange.getRequest().getMethod(), exchange.getRequest().getURI(), exchange.getRequest().getHeaders(),
                exchange.getRequest().getBody());

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            log.info(RESPONSE_LOG_TEMPLATE, exchange.getResponse().getStatusCode(), exchange.getResponse().getHeaders(), exchange.getResponse());
        }));
    }

}
