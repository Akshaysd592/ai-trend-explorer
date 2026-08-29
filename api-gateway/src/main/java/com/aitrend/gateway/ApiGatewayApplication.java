package com.aitrend.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.Map;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

@SpringBootApplication
public class ApiGatewayApplication {

    private static final Logger log = LoggerFactory.getLogger(ApiGatewayApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    private String normalizeUrl(String rawUrl, int defaultPort) {
        if (rawUrl == null || rawUrl.isBlank()) {
            return "http://localhost:" + defaultPort;
        }
        rawUrl = rawUrl.trim();
        if (rawUrl.startsWith("http://") || rawUrl.startsWith("https://")) {
            return rawUrl.endsWith("/") ? rawUrl.substring(0, rawUrl.length() - 1) : rawUrl;
        }
        if (rawUrl.contains(".onrender.com")) {
            return "https://" + rawUrl;
        }
        if (!rawUrl.contains(".") && !rawUrl.equalsIgnoreCase("localhost")) {
            return "https://" + rawUrl + ".onrender.com";
        }
        return "http://" + rawUrl + ":" + defaultPort;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder,
                                           @Value("${AUTH_SERVICE_URL:}") String authUrl,
                                           @Value("${TREND_SERVICE_URL:}") String trendUrl,
                                           @Value("${AI_ANALYSIS_SERVICE_URL:}") String aiUrl) {
        String finalAuthUrl = normalizeUrl(authUrl, 8082);
        String finalTrendUrl = normalizeUrl(trendUrl, 8081);
        String finalAiUrl = normalizeUrl(aiUrl, 8083);

        log.info("ApiGateway routing: AUTH -> {}, TREND -> {}, AI -> {}", finalAuthUrl, finalTrendUrl, finalAiUrl);

        return builder.routes()
                .route("auth-service", r -> r.path("/api/v1/auth/**").uri(finalAuthUrl))
                .route("auth-docs", r -> r.path("/v3/api-docs/auth/**")
                        .filters(f -> f.rewritePath("/v3/api-docs/auth(?<segment>/?.*)", "/v3/api-docs${segment}"))
                        .uri(finalAuthUrl))
                .route("trend-service", r -> r.path("/api/v1/trends/**").uri(finalTrendUrl))
                .route("trend-docs", r -> r.path("/v3/api-docs/trends/**")
                        .filters(f -> f.rewritePath("/v3/api-docs/trends(?<segment>/?.*)", "/v3/api-docs${segment}"))
                        .uri(finalTrendUrl))
                .route("ai-analysis-service", r -> r.path("/api/v1/analysis/**").uri(finalAiUrl))
                .route("ai-analysis-docs", r -> r.path("/v3/api-docs/analysis/**")
                        .filters(f -> f.rewritePath("/v3/api-docs/analysis(?<segment>/?.*)", "/v3/api-docs${segment}"))
                        .uri(finalAiUrl))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> rootEndpoint(@Value("${AUTH_SERVICE_URL:}") String authUrl,
                                                       @Value("${TREND_SERVICE_URL:}") String trendUrl,
                                                       @Value("${AI_ANALYSIS_SERVICE_URL:}") String aiUrl) {
        return RouterFunctions.route(GET("/"), request ->
                ServerResponse.ok().bodyValue(Map.of(
                        "status", "UP",
                        "service", "AI Trend Explorer API Gateway",
                        "version", "1.0.0",
                        "message", "API Gateway is running and routing microservices cleanly.",
                        "routes", Map.of(
                                "auth", normalizeUrl(authUrl, 8082),
                                "trend", normalizeUrl(trendUrl, 8081),
                                "analysis", normalizeUrl(aiUrl, 8083)
                        ),
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
