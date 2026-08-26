package com.aitrend.trend.application.service;

import com.aitrend.trend.application.event.IngestionCompletedEvent;
import com.aitrend.trend.application.port.in.IngestTrendsUseCase;
import com.aitrend.trend.application.port.in.IngestionResult;
import com.aitrend.trend.application.port.out.FetchGitHubTrendsPort;
import com.aitrend.trend.application.port.out.FetchHuggingFaceTrendsPort;
import com.aitrend.trend.application.port.out.TrendEventPublisherPort;
import com.aitrend.trend.application.port.out.TrendRepositoryPort;
import com.aitrend.trend.domain.model.Trend;
import com.aitrend.trend.domain.service.TrendScoringCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class IngestTrendsService implements IngestTrendsUseCase {

    private static final Logger log = LoggerFactory.getLogger(IngestTrendsService.class);

    private final FetchGitHubTrendsPort gitHubTrendsPort;
    private final FetchHuggingFaceTrendsPort huggingFaceTrendsPort;
    private final TrendRepositoryPort trendRepositoryPort;
    private final TrendScoringCalculator scoringCalculator;
    private final TrendEventPublisherPort kafkaEventPublisher;
    private final ApplicationEventPublisher applicationEventPublisher;

    public IngestTrendsService(FetchGitHubTrendsPort gitHubTrendsPort,
                               FetchHuggingFaceTrendsPort huggingFaceTrendsPort,
                               TrendRepositoryPort trendRepositoryPort,
                               TrendScoringCalculator scoringCalculator,
                               TrendEventPublisherPort kafkaEventPublisher,
                               ApplicationEventPublisher applicationEventPublisher) {
        this.gitHubTrendsPort = gitHubTrendsPort;
        this.huggingFaceTrendsPort = huggingFaceTrendsPort;
        this.trendRepositoryPort = trendRepositoryPort;
        this.scoringCalculator = scoringCalculator;
        this.kafkaEventPublisher = kafkaEventPublisher;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    @CacheEvict(value = "trends", allEntries = true)
    public IngestionResult ingestTrends() {
        log.info("Starting multi-platform AI trend ingestion process...");

        List<Trend> githubTrends = gitHubTrendsPort.fetchTrendingRepositories();
        List<Trend> huggingFaceTrends = huggingFaceTrendsPort.fetchTrendingModels();
        List<Trend> savedTrendsList = new ArrayList<>();

        int savedGithub = 0;
        for (Trend rawTrend : githubTrends) {
            Trend scored = scoringCalculator.applyScoring(rawTrend);
            Trend saved = trendRepositoryPort.save(scored);
            savedTrendsList.add(saved);
            kafkaEventPublisher.publishTrendIngestedEvent(saved);
            savedGithub++;
        }

        int savedHuggingFace = 0;
        for (Trend rawTrend : huggingFaceTrends) {
            Trend scored = scoringCalculator.applyScoring(rawTrend);
            Trend saved = trendRepositoryPort.save(scored);
            savedTrendsList.add(saved);
            kafkaEventPublisher.publishTrendIngestedEvent(saved);
            savedHuggingFace++;
        }

        int totalSaved = savedGithub + savedHuggingFace;
        log.info("Ingestion completed. Total items persisted: {} (GitHub: {}, HuggingFace: {})",
                totalSaved, savedGithub, savedHuggingFace);

        // Publish local spring event as well for local listeners
        applicationEventPublisher.publishEvent(new IngestionCompletedEvent(savedTrendsList));

        return new IngestionResult(totalSaved, savedGithub, savedHuggingFace, Instant.now());
    }
}
