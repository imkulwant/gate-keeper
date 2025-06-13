package com.kulsin.gate_keeper.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class TracingFilter implements GlobalFilter {

	private static final Logger log = LoggerFactory.getLogger(TracingFilter.class);

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		return chain.filter(exchange).doFirst(() -> {
			log.info("Request - TraceId: {}, SpanId: {}", MDC.get("traceId"), MDC.get("spanId"));
		}).then(Mono.fromRunnable(() -> {
			log.info("Response - TraceId: {}, SpanId: {}", MDC.get("traceId"), MDC.get("spanId"));
		}));
	}

}
