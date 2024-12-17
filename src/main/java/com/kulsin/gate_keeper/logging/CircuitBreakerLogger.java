package com.kulsin.gate_keeper.logging;

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
		circuitBreakerRegistry.getAllCircuitBreakers().forEach(circuitBreaker -> {
			circuitBreaker.getEventPublisher()
				.onStateTransition(event -> logEvent("Circuit Breaker State Transition.", event.getCircuitBreakerName(),
						event.getEventType().toString(), "INFO"))
				.onError(event -> logEvent("Circuit Breaker Error.", event.getCircuitBreakerName(),
						event.getEventType().toString(), "ERROR"))
				.onSuccess(event -> logEvent("Circuit Breaker Success.", event.getCircuitBreakerName(),
						event.getEventType().toString(), "INFO"))
				.onIgnoredError(event -> logEvent("Circuit Breaker Ignored Error.", event.getCircuitBreakerName(),
						event.getEventType().toString(), "WARN"))
				.onReset(event -> logEvent("Circuit Breaker Reset.", event.getCircuitBreakerName(),
						event.getEventType().toString(), "INFO"))
				.onCallNotPermitted(event -> logEvent("Circuit Breaker Call Not Permitted.",
						event.getCircuitBreakerName(), event.getEventType().toString(), "WARN"));
		});
	}

	private static void logEvent(String message, String circuitBreakerName, String eventType, String logLevel) {
		if ("ERROR".equals(logLevel)) {
			log.error("{} circuitBreakerName: {}, eventType: {}", message, circuitBreakerName, eventType);
		}
		else if ("WARN".equals(logLevel)) {
			log.warn("{} circuitBreakerName: {}, eventType: {}", message, circuitBreakerName, eventType);
		}
		else {
			log.info("{} circuitBreakerName: {}, eventType: {}", message, circuitBreakerName, eventType);
		}
	}

}
