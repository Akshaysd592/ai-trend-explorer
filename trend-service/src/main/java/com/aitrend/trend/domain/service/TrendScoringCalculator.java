package com.aitrend.trend.domain.service;

import com.aitrend.trend.domain.model.Trend;
import org.springframework.stereotype.Component;

@Component
public class TrendScoringCalculator {

    public double calculateTrendScore(int stars, int forks) {
        return (stars * 0.6) + (forks * 0.4);
    }

    public Trend applyScoring(Trend trend) {
        double score = calculateTrendScore(trend.getStars(), trend.getForks());
        return new Trend(
                trend.getId(),
                trend.getTitle(),
                trend.getDescription(),
                trend.getRepositoryUrl(),
                trend.getSource(),
                trend.getStars(),
                trend.getForks(),
                trend.getLanguage(),
                trend.getTopics(),
                score,
                trend.getAiCategory(),
                trend.getAiSummary(),
                trend.getCreatedAt(),
                trend.getUpdatedAt()
        );
    }
}

