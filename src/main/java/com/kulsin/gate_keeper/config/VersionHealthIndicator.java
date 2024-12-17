package com.kulsin.gate_keeper.config;

import org.springframework.boot.actuate.health.Health;

import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VersionHealthIndicator implements HealthIndicator {

	private final BuildProperties buildProperties;

	public VersionHealthIndicator(BuildProperties buildProperties) {
		this.buildProperties = buildProperties;
	}

	@Override
	public Health health() {
		return Health.up().withDetail("version", buildProperties.getVersion()).build();
	}

}
