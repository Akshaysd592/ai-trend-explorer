package com.aitrend.analysis.adapter.out.rest;

import com.aitrend.analysis.application.port.out.TrendServiceClientPort;
import com.aitrend.analysis.domain.model.AiAnalysisResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class TrendServiceClientAdapter implements TrendServiceClientPort {

    private static final Logger log = LoggerFactory.getLogger(TrendServiceClientAdapter.class);

    private final RestClient restClient;

    public TrendServiceClientAdapter(
            @Value("${app.trend-service.url:http://localhost:8081}") String trendServiceUrl
    ) {
        this.restClient = RestClient.builder()
                .baseUrl(trendServiceUrl)
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public void updateTrendAiMetadata(Long trendId, AiAnalysisResult result) {
        try {
            Map<String, String> requestBody = Map.of(
                    "aiCategory", result.category(),
                    "aiSummary", result.summary()
            );

            log.info("Sending PATCH /api/v1/trends/{}/ai-metadata to trend-service", trendId);

            restClient.patch()
                    .uri("/api/v1/trends/{id}/ai-metadata", trendId)
                    .body(requestBody)
                    .retrieve()
                    .toBodilessEntity();

            log.info("Successfully updated trend id: {} in trend-service", trendId);

        } catch (Exception e) {
            log.error("Failed to update AI metadata in trend-service for trend id: {}: {}",
                    trendId, e.getMessage());
        }
    }
}
