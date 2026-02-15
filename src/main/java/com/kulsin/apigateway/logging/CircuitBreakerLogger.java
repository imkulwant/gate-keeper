package com.kulsin.apigateway.logging;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CircuitBreakerLogger {

	private static final Logger log = LoggerFactory.getLogger(CircuitBreakerLogger.class);

	private final CircuitBreakerRegistry circuitBreakerRegistry;

	public CircuitBreakerLogger(CircuitBreakerRegistry circuitBreakerRegistry) {
		this.circuitBreakerRegistry = circuitBreakerRegistry;
	}

	@PostConstruct
	public void setupCircuitBreakerLogging() {
		circuitBreakerRegistry.getAllCircuitBreakers().forEach(this::attachLogging);
		circuitBreakerRegistry.getEventPublisher().onEntryAdded(event -> {
			CircuitBreaker circuitBreaker = event.getAddedEntry();
			attachLogging(circuitBreaker);
		});
	}

	private void attachLogging(CircuitBreaker circuitBreaker) {
		log.info("Attaching circuit breaker logging for '{}'", circuitBreaker.getName());
		circuitBreaker.getEventPublisher()
			.onStateTransition(event -> log.info("Circuit breaker '{}' state transition: {}",
					event.getCircuitBreakerName(), event.getStateTransition()))
			.onError(event -> log.error("Circuit breaker '{}' error event: {} message: {}",
					event.getCircuitBreakerName(), event.getEventType(), event.getThrowable().getMessage()))
			.onSuccess(event -> log.debug("Circuit breaker '{}' success event: {}", event.getCircuitBreakerName(),
					event.getEventType()))
			.onIgnoredError(event -> log.warn("Circuit breaker '{}' ignored error event: {} message: {}",
					event.getCircuitBreakerName(), event.getEventType(), event.getThrowable().getMessage()))
			.onReset(event -> log.debug("Circuit breaker '{}' reset event: {}", event.getCircuitBreakerName(),
					event.getEventType()))
			.onCallNotPermitted(event -> log.warn("Circuit breaker '{}' call not permitted: {}",
					event.getCircuitBreakerName(), event.getEventType()));
	}

}
