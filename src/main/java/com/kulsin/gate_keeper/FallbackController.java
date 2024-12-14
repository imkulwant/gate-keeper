package com.kulsin.gate_keeper;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

	@GetMapping("/fallback")
	public ResponseEntity<String> fallback() {
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
			.body("Service is temporarily unavailable! Please try again later.");
	}

}
