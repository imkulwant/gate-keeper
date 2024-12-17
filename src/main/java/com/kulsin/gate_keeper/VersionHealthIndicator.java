package com.kulsin.gate_keeper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;

import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VersionHealthIndicator implements HealthIndicator {

	@Autowired
	private BuildProperties buildProperties;

	@Override
	public Health health() {
		return Health.up().withDetail("version", buildProperties.getVersion()).build();
	}

}
