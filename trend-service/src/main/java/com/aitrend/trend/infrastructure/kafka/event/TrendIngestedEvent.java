package com.aitrend.trend.infrastructure.kafka.event;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

/**
 * Event published to Kafka topic (ai.trends.ingested) when a raw trend
 * is scraped and persisted from GitHub or HuggingFace.
 */
public record TrendIngestedEvent(
        Long trendId,
        String title,
        String description,
        String repositoryUrl,
        String source,
        Integer stars,
        Integer forks,
        String language,
        List<String> topics,
        Double trendScore,
        Instant timestamp
) implements Serializable {
    public TrendIngestedEvent {
        if (topics == null) {
            topics = List.of();
        }
        if (timestamp == null) {
            timestamp = Instant.now();
        }
    }
}
