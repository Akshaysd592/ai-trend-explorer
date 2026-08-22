package com.aitrend.trend.adapter.out.huggingface;

import com.aitrend.trend.application.port.out.FetchHuggingFaceTrendsPort;
import com.aitrend.trend.domain.model.SourceType;
import com.aitrend.trend.domain.model.Trend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class HuggingFaceApiClientAdapter implements FetchHuggingFaceTrendsPort {

    private static final Logger log = LoggerFactory.getLogger(HuggingFaceApiClientAdapter.class);
    private final RestClient restClient;

    public HuggingFaceApiClientAdapter() {
        this.restClient = RestClient.builder()
                .baseUrl("https://huggingface.co")
                .defaultHeader("User-Agent", "AI-Trend-Explorer-Service")
                .build();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Trend> fetchTrendingModels() {
        log.info("Fetching trending open-weights models from Hugging Face Hub API...");
        try {
            List<Map<String, Object>> response = restClient.get()
                    .uri("/api/models?sort=downloads&direction=-1&limit=20")
                    .retrieve()
                    .body(List.class);

            if (response == null || response.isEmpty()) {
                log.warn("Hugging Face API returned empty response list");
                return List.of();
            }

            List<Trend> trends = new ArrayList<>();

            for (Map<String, Object> item : response) {
                String modelId = (String) item.get("id");
                Integer downloads = item.get("downloads") != null ? ((Number) item.get("downloads")).intValue() : 0;
                Integer likes = item.get("likes") != null ? ((Number) item.get("likes")).intValue() : 0;
                String pipelineTag = item.get("pipeline_tag") != null ? (String) item.get("pipeline_tag") : "text-generation";
                List<String> tags = item.get("tags") != null ? (List<String>) item.get("tags") : List.of();

                String repoUrl = "https://huggingface.co/" + (modelId != null ? modelId : "");
                String description = "Trending Hugging Face Model (" + pipelineTag + ")";

                Trend trend = new Trend(
                        null,
                        modelId != null ? modelId : "unknown/model",
                        description,
                        repoUrl,
                        SourceType.HUGGING_FACE,
                        likes,
                        downloads,
                        "Python",
                        tags.stream().limit(5).toList(),
                        0.0,
                        "Open-Weights LLM & Models",
                        description,
                        null,
                        null
                );
                trends.add(trend);
            }
            log.info("Successfully fetched {} trending models from Hugging Face", trends.size());
            return trends;
        } catch (Exception e) {
            log.error("Failed to fetch trends from Hugging Face API: {}", e.getMessage(), e);
            return List.of();
        }
    }
}

