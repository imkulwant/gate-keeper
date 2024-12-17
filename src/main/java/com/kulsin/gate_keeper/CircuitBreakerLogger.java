package com.kulsin.gate_keeper;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CircuitBreakerLogger {

	@Autowired
	private CircuitBreakerRegistry circuitBreakerRegistry;

	private static final Logger log = LoggerFactory.getLogger(CircuitBreakerLogger.class);

	@PostConstruct
	public void setupCircuitBreakerLogging() {
		log.info("Setting up circuit breaker logging");
		circuitBreakerRegistry.getAllCircuitBreakers()
			.stream()
			.filter(p -> p.getName().equals("defaultCircuitBreaker"))
			.forEach(circuitBreaker -> circuitBreaker.getEventPublisher()
				.onStateTransition(event -> log.info("Circuit Breaker State Transition: {}", event))
				.onError(event -> log.error("Circuit Breaker Error: {}", event))
				.onSuccess(event -> log.info("Circuit Breaker Success: {}", event))
				.onIgnoredError(event -> log.warn("Circuit Breaker Ignored Error: {}", event))
				.onReset(event -> log.info("Circuit Breaker Reset: {}", event))
				.onCallNotPermitted(event -> log.warn("Circuit Breaker Call Not Permitted: {}", event)));
	}

}
