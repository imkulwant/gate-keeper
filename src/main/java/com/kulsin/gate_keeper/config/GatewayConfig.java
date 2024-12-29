package com.kulsin.gate_keeper.config;

import brave.Tracing;
import brave.propagation.CurrentTraceContext;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.brave.bridge.BraveCurrentTraceContext;
import io.micrometer.tracing.brave.bridge.BraveTracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

	@Bean
	public Tracer tracer() {
		Tracing tracing = Tracing.newBuilder().build();
		brave.Tracer braveTracer = tracing.tracer();
		CurrentTraceContext currentTraceContext = tracing.currentTraceContext();
		io.micrometer.tracing.CurrentTraceContext braveCurrentTraceContext = BraveCurrentTraceContext
			.fromBrave(currentTraceContext);
		return new BraveTracer(braveTracer, braveCurrentTraceContext);
	}

}
