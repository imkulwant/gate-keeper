package com.kulsin.gate_keeper.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

	private static final Logger logger = LoggerFactory.getLogger(FallbackController.class);

	@GetMapping("/fallback")
	public ResponseEntity<String> fallback() {
		logger.error("Service is temporarily unavailable! Please try again later.");
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
			.body("Service is temporarily unavailable! Please try again later.");
	}

}
