package com.aitrend.analysis.adapter.in.kafka.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

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
    }
}
