package com.aitrend.analysis.domain.model;

import java.util.List;

public record TrendAnalysisRequest(
        Long trendId,
        String title,
        String description,
        String repositoryUrl,
        String language,
        List<String> topics,
        Integer stars,
        Integer forks
) {
    public TrendAnalysisRequest {
        if (topics == null) {
            topics = List.of();
        }
    }
}
