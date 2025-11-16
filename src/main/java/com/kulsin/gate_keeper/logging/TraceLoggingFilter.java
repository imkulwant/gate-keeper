package com.kulsin.gate_keeper.logging;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

@Component
public class TraceLoggingFilter implements GlobalFilter {

	private static final Logger log = LoggerFactory.getLogger(TraceLoggingFilter.class);

	private static final String REQ_LOG_TEMPLATE = """
			\n
			----------------Request Details-------------------------
			Request Method: {}
			Request URI: {}
			Request Headers: {}
			Request Body: {}
			--------------------------------------------------------
			""";

	private static final String RES_LOG_TEMPLATE = """
			\n
			----------------Response Details------------------------
			Response Status: {}
			Response Headers: {}
			Response Body: {}
			--------------------------------------------------------
			""";

	public static final String X_B3_TRACE_ID = "X-B3-TraceId";

	public static final String X_B3_SPAN_ID = "X-B3-SpanId";

	public static final String NA = "N/A";

	private final Tracer tracer;

	public TraceLoggingFilter(Tracer tracer) {
		this.tracer = tracer;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		// Access current span reactively inside context
		return Mono.deferContextual(contextView -> {

			Span currentSpan = tracer.currentSpan();
			String traceId = currentSpan != null ? currentSpan.context().traceId() : NA;
			String spanId = currentSpan != null ? currentSpan.context().spanId() : NA;

			log.info(REQ_LOG_TEMPLATE, exchange.getRequest().getMethod(), exchange.getRequest().getURI(),
					exchange.getRequest().getHeaders(), exchange.getRequest().getBody());

			// Add IDs to response headers for visibility
			exchange.getResponse().getHeaders().add(X_B3_TRACE_ID, traceId);
			exchange.getResponse().getHeaders().add(X_B3_SPAN_ID, spanId);

			return chain.filter(exchange)
				// Logging the response status with trace info after response
				.doOnSuccess(aVoid -> {
					log.info(RES_LOG_TEMPLATE, exchange.getResponse().getStatusCode(),
							exchange.getResponse().getHeaders(), "");
				});
		})
			.contextWrite(Context.of("traceId",
					tracer.currentSpan() != null ? tracer.currentSpan().context().traceId() : NA));

	}

}
