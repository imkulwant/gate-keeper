package com.kulsin.gate_keeper.gateway;

import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

   /* @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("service1", r -> r.path("/service1/**")
                        .filters(f -> f.rewritePath("/service1(?<remaining>/?.*)", "/${remaining}"))
                        .uri("http://localhost:8081"))
                .route("service2", r -> r.path("/service2/**")
                        .filters(f -> f.rewritePath("/service2(?<remaining>/?.*)", "/${remaining}"))
                        .uri("http://localhost:8082"))
                .build();
    }
*/
}
