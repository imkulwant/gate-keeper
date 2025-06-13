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

	private static final String LOG_TEMPLATE = """
			\n--------------------------------------------------------
			Request Details
			--------------------------------------------------------
			Request Method: {}
			Request URI: {}
			Request Headers: {}
			Request Body: {}
			TraceId: {}
			SpanId: {}
			--------------------------------------------------------
			Response Details
			--------------------------------------------------------
			Response Status: {}
			Response Headers: {}
			Response Body: {}
			TraceId: {}
			SpanId: {}
			--------------------------------------------------------
			""";

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		return Mono.deferContextual(contextView -> {
			String traceId = contextView.getOrDefault("traceId", "");
			String spanId = contextView.getOrDefault("spanId", "");

			log.info("Request - TraceId: {}, SpanId: {}", traceId, spanId);

			return chain.filter(exchange).then(Mono.fromRunnable(() -> {
				log.info(LOG_TEMPLATE, exchange.getRequest().getMethod(), exchange.getRequest().getURI(),
						exchange.getRequest().getHeaders(), traceId, spanId, exchange.getResponse().getStatusCode(),
						exchange.getResponse().getHeaders(), traceId, spanId);
			}));
		});
	}

}
