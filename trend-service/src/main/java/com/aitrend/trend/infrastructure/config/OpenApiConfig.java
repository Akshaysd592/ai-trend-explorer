package com.aitrend.trend.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI trendServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Trend Service API")
                        .description("Multi-Platform AI Trend Aggregation, Scoring, Search, and Pagination Engine")
                        .version("1.0.0"));
    }
}
