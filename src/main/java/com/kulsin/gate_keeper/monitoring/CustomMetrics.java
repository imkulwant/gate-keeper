package com.kulsin.gate_keeper.monitoring;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class CustomMetrics {

    private final MeterRegistry meterRegistry;

    public CustomMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        meterRegistry.gauge("custom.metric", 100);
    }

    public void incrementCustomMetrics() {
        meterRegistry.counter("custom.metric").increment();
    }

}
