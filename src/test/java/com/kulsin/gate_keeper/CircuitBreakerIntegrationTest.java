package com.kulsin.gate_keeper;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CircuitBreakerIntegrationTest {

	@Autowired
	private TestRestTemplate restTemplate;

	private WireMockServer wireMockServer;

	@BeforeEach
	public void setUp() {
		wireMockServer = new WireMockServer(8081);
		wireMockServer.start();
		WireMock.configureFor("localhost", 8081);
		WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo("/api/ping")).willReturn(WireMock.serverError()));
	}

	@Test
	void testCircuitBreakerOnFailure() throws InterruptedException {
		// Simulate service failure by making repeated requests
		for (int i = 0; i < 10; i++) {
			ResponseEntity<String> response = restTemplate.getForEntity("/service1/api/ping", String.class);

			assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode()); // Ensure
																						// it's
																						// not
																						// falling
																						// back
																						// yet
		}

		Thread.sleep(5000); // Adjust this based on your waitDurationInOpenState setting

		ResponseEntity<String> response = restTemplate.getForEntity("/service1/api/ping", String.class);
		assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode()); // Expecting
																				// fallback
																				// response

	}

	@AfterEach
	public void tearDown() {
		wireMockServer.stop();
	}

}
