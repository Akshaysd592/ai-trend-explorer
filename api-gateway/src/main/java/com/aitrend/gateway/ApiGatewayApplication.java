package com.aitrend.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.Map;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    @Bean
    public RouterFunction<ServerResponse> rootEndpoint() {
        return RouterFunctions.route(GET("/"), request ->
                ServerResponse.ok().bodyValue(Map.of(
                        "status", "UP",
                        "service", "AI Trend Explorer API Gateway",
                        "version", "1.0.0",
                        "message", "API Gateway is running and routing microservices cleanly.",
                        "endpoints", Map.of(
                                "health", "/actuator/health",
                                "auth", "/api/v1/auth",
                                "trends", "/api/v1/trends",
                                "analysis", "/api/v1/analysis"
                        )
                ))
        );
    }
}
