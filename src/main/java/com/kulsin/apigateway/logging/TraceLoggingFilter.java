package com.kulsin.apigateway.logging;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;

/**
 * {@code TraceLoggingFilter} is a Spring Cloud Gateway {@link GlobalFilter} responsible
 * for correlating and logging distributed trace information for every incoming request
 * passing through the gateway.
 * <p>
 * The filter enhances observability by:
 * <ul>
 * <li>Attaching B3 tracing headers (<code>X-B3-TraceId</code>, <code>X-B3-SpanId</code>)
 * to responses.</li>
 * <li>Logging incoming requests and outgoing responses along with trace and span
 * IDs.</li>
 * <li>Redacting sensitive HTTP headers (e.g., authorization, cookies) from logs.</li>
 * <li>Measuring total exchange duration using nanosecond precision.</li>
 * </ul>
 * <p>
 * This filter operates in a reactive context using Project Reactor’s {@link Mono} API,
 * ensuring non-blocking tracing and logging behavior.
 */
@Component
public class TraceLoggingFilter implements GlobalFilter {

	private static final Logger log = LoggerFactory.getLogger(TraceLoggingFilter.class);

	public static final String X_B3_TRACE_ID = "X-B3-TraceId";

	public static final String X_B3_SPAN_ID = "X-B3-SpanId";

	public static final String NA = "N/A";

	public static final String REDACTED = "[REDACTED]";

	private static final Set<String> SENSITIVE_HEADERS = Set.of(HttpHeaders.AUTHORIZATION.toLowerCase(Locale.ROOT),
			HttpHeaders.COOKIE.toLowerCase(Locale.ROOT), HttpHeaders.SET_COOKIE.toLowerCase(Locale.ROOT),
			"proxy-authorization", "x-api-key");

	private final Tracer tracer;

	public TraceLoggingFilter(Tracer tracer) {
		this.tracer = tracer;
	}

	/**
	 * Applies tracing-aware logging to each gateway exchange.
	 * <p>
	 * Records request context (method, URI, trace/span IDs), then delegates processing to
	 * the filter chain. Completion or error events trigger additional logging including
	 * response status and total latency.
	 * @param exchange The current server exchange.
	 * @param chain The gateway filter chain.
	 * @return A {@link Mono} that completes once the chain finishes processing.
	 */
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		return Mono.defer(() -> {
			long startTimeNanos = System.nanoTime();
			URI uri = exchange.getRequest().getURI();
			String method = exchange.getRequest().getMethod() != null ? exchange.getRequest().getMethod().name() : NA;

			TraceContextInfo requestTrace = getCurrentTraceInfo();
			setB3Headers(exchange, requestTrace);

			if (log.isDebugEnabled()) {
				log.debug("Incoming gateway request method={} uri={} headers={}", method, uri,
						redactHeaders(exchange.getRequest().getHeaders()));
			}

			return chain.filter(exchange)
				.doOnError(error -> log.warn("Gateway request failed method={} uri={} error={}", method, uri,
						error.getMessage()))
				.doFinally(signalType -> logExchangeCompletion(exchange, startTimeNanos, method, uri, signalType));
		});
	}

	/**
	 * Logs completion details for the given exchange, including the response status code,
	 * total duration, completion signal type (e.g., {@code ON_COMPLETE},
	 * {@code ON_ERROR}), and the current trace/span identifiers.
	 */
	private void logExchangeCompletion(ServerWebExchange exchange, long startTimeNanos, String method, URI uri,
			SignalType signalType) {
		long durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTimeNanos);
		HttpStatusCode statusCode = exchange.getResponse().getStatusCode();

		log.info("Gateway exchange completed method={} uri={} status={} durationMs={} signal={}", method, uri,
				statusCode != null ? statusCode.value() : NA, durationMs, signalType.name());

		if (log.isDebugEnabled()) {
			log.debug("Gateway response headers method={} uri={} headers={}", method, uri,
					redactHeaders(exchange.getResponse().getHeaders()));
		}
	}

	/**
	 * Retrieves the current trace and span identifiers from the active {@link Span}.
	 * @return A {@link TraceContextInfo} record containing trace and span IDs, or
	 * {@code N/A} if no span is active.
	 */
	private TraceContextInfo getCurrentTraceInfo() {
		Span currentSpan = tracer.currentSpan();
		if (currentSpan == null || currentSpan.context() == null) {
			return new TraceContextInfo(NA, NA);
		}

		return new TraceContextInfo(currentSpan.context().traceId(), currentSpan.context().spanId());
	}

	/**
	 * Sets the B3 trace and span identifiers as response headers, allowing downstream
	 * consumers to correlate logs and traces.
	 */
	private void setB3Headers(ServerWebExchange exchange, TraceContextInfo traceContextInfo) {
		if (!NA.equals(traceContextInfo.traceId())) {
			exchange.getResponse().getHeaders().set(X_B3_TRACE_ID, traceContextInfo.traceId());
		}
		if (!NA.equals(traceContextInfo.spanId())) {
			exchange.getResponse().getHeaders().set(X_B3_SPAN_ID, traceContextInfo.spanId());
		}
	}

	/**
	 * Produces a copy of the headers map where sensitive headers are replaced with
	 * {@code [REDACTED]}.
	 * @param headers The HTTP headers to sanitize.
	 * @return A redacted map suitable for safe logging.
	 */
	private Map<String, List<String>> redactHeaders(HttpHeaders headers) {
		Map<String, List<String>> redacted = new LinkedHashMap<>();
		headers.forEach((name, values) -> {
			if (isSensitiveHeader(name)) {
				redacted.put(name, List.of(REDACTED));
				return;
			}
			redacted.put(name, values);
		});
		return redacted;
	}

	/**
	 * Checks whether a given header name is considered sensitive.
	 * @param headerName The header name to inspect.
	 * @return {@code true} if the header should be masked in logs; {@code false}
	 * otherwise.
	 */
	private boolean isSensitiveHeader(String headerName) {
		return SENSITIVE_HEADERS.contains(headerName.toLowerCase(Locale.ROOT));
	}

	/**
	 * Immutable record representing trace correlation data.
	 *
	 * @param traceId The trace identifier, possibly {@code N/A}.
	 * @param spanId The span identifier, possibly {@code N/A}.
	 */
	private record TraceContextInfo(String traceId, String spanId) {
	}

}
