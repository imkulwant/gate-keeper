package com.kulsin.gate_keeper;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application.yml")
public class GatewayIntegrationTest {

	private static WireMockServer wireMockServer;

	@Autowired
	private WebTestClient webTestClient;

	@Autowired
	private CircuitBreakerRegistry circuitBreakerRegistry;

	@BeforeAll
	public static void setUp() {
		wireMockServer = new WireMockServer(8081);
		wireMockServer.start();
		WireMock.configureFor("localhost", 8081);
	}

	@Test
	void testBasicRouting() {
		WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo("/api/ping")).willReturn(WireMock.ok("Pong")));

		webTestClient.get()
			.uri("/service1/api/ping")
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody(String.class)
			.isEqualTo("Pong");
	}

	@Test
	void testGatewayTimeout() {
		WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo("/api/ping"))
			.willReturn(WireMock.ok("Pong").withFixedDelay(10000)));

		webTestClient.get().uri("/service1/api/ping").exchange().expectStatus().isEqualTo(HttpStatus.GATEWAY_TIMEOUT);
	}

	@Test
	void testServiceUnavailable() {
		WireMock.stubFor(WireMock.get("/api/ping").willReturn(WireMock.serviceUnavailable()));

		webTestClient.get()
			.uri("/service1/api/ping")
			.exchange()
			.expectStatus()
			.isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
	}

	@Test
	void testInternalServerError() {
		WireMock.stubFor(WireMock.get("/api/error").willReturn(WireMock.serverError()));

		webTestClient.get()
			.uri("/service1/api/error")
			.exchange()
			.expectStatus()
			.isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Test
	void testCircuitBreaker() {

		WireMock.stubFor(
				WireMock.get("/api/ping").willReturn(WireMock.aResponse().withFault(Fault.CONNECTION_RESET_BY_PEER)));

		// Assuming circuit breaker opens after 5 failures
		for (int i = 0; i < 5; i++) {
			webTestClient.get().uri("/service1/api/ping").exchange().expectStatus().is5xxServerError();
		}

		CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("defaultCircuitBreaker");

		assertEquals(CircuitBreaker.State.OPEN, circuitBreaker.getState());

		// Next request should be short-circuited
		webTestClient.get()
			.uri("/service1/api/ping")
			.exchange()
			.expectStatus()
			.isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
	}

	@AfterAll
	public static void tearDownWireMock() {
		wireMockServer.stop();
	}

	@AfterEach
	public void resetCircuitBreaker() {
		CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("defaultCircuitBreaker");
		circuitBreaker.reset();
	}

}
