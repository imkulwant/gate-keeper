package com.kulsin.gate_keeper.ratelimit;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * {@code IPAddressKeyResolver} is a Spring Cloud Gateway {@link KeyResolver}
 * implementation that resolves the rate limiting key based on the client's IP
 * address.
 * <p>
 * This resolver first attempts to extract the client IP from the
 * "X-Forwarded-For"
 * HTTP header. This header is commonly set by proxies or load balancers to
 * represent
 * the original user's IP address. If the header is absent or blank, the
 * resolver
 * falls back to using the remote address from the actual network connection.
 * <p>
 * If neither the header nor the remote address provides a valid result,
 * the resolver returns a constant value {@code "unknown"} to ensure a key is
 * always provided for rate limiting purposes.
 * <p>
 * This strategy helps in correctly identifying end users when the gateway is
 * deployed behind proxies, improving rate limiting accuracy for multi-tenant
 * environments.
 *
 * <pre>
 * Example usage:
 * &nbsp;&nbsp;spring.cloud.gateway.filter.request-rate-limiter.key-resolver: "#{@ipAddressKeyResolver}"
 * </pre>
 *
 * @author Kulwant Singh
 * @see org.springframework.cloud.gateway.filter.ratelimit.KeyResolver
 */
@Component
public class IPAddressKeyResolver implements KeyResolver {

	/**
	 * Resolve the request limiting key based on the client IP address.
	 * <p>
	 * Priority:
	 * <ul>
	 * <li>First, tries the "X-Forwarded-For" header and takes its first value.</li>
	 * <li>If not present, falls back to the remote address of the TCP
	 * connection.</li>
	 * <li>If both fail, returns "unknown".</li>
	 * </ul>
	 *
	 * @param exchange the current server exchange context
	 * @return a {@link Mono} emitting the resolved key as a {@link String}
	 */
	@Override
	public Mono<String> resolve(ServerWebExchange exchange) {

		String xff = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
		if (xff != null && !xff.isBlank()) {
			String ip = xff.split(",")[0].trim();
			return Mono.just(ip);
		}

		if (exchange.getRequest().getRemoteAddress() != null
				&& exchange.getRequest().getRemoteAddress().getAddress() != null) {
			return Mono.just(exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
		}

		return Mono.just("unknown");
	}

}
