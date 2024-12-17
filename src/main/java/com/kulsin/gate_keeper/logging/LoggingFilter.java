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
			\n------------------------------------------------
			Request Method: {}
			Request URI: {}
			Request Headers: {}
			Request Body: {}
			------------------------------------------------
			Response Status: {}
			Response Headers: {}
			Response Body: {}
			------------------------------------------------
			""";

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		return chain.filter(exchange).then(Mono.fromRunnable(() -> {
			log.info(LOG_TEMPLATE, exchange.getRequest().getMethod(), exchange.getRequest().getURI(),
					exchange.getRequest().getHeaders(), exchange.getRequest().getBody(),
					exchange.getResponse().getStatusCode(), exchange.getResponse().getHeaders(),
					exchange.getResponse());
		}));
	}

}
