package com.aitrend.trend.infrastructure.scheduler;

import com.aitrend.trend.application.port.in.GetTrendsQuery;
import com.aitrend.trend.application.port.in.IngestTrendsUseCase;
import com.aitrend.trend.application.port.in.IngestionResult;
import com.aitrend.trend.application.port.in.PagedResult;
import com.aitrend.trend.application.port.in.TrendUseCase;
import com.aitrend.trend.domain.model.Trend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Ensures live trend data is fetched automatically on application startup
 * if the database only contains initial sample seed data (<= 5 trends).
 */
@Component
public class InitialTrendIngestionRunner {

    private static final Logger log = LoggerFactory.getLogger(InitialTrendIngestionRunner.class);

    private final TrendUseCase trendUseCase;
    private final IngestTrendsUseCase ingestTrendsUseCase;

    public InitialTrendIngestionRunner(TrendUseCase trendUseCase, IngestTrendsUseCase ingestTrendsUseCase) {
        this.trendUseCase = trendUseCase;
        this.ingestTrendsUseCase = ingestTrendsUseCase;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        try {
            PagedResult<Trend> currentTrends = trendUseCase.getTrends(
                    new GetTrendsQuery(null, null, null, 0, 10, "stars", "desc")
            );

            log.info("Checking database state on startup... Current total trends in DB: {}", currentTrends.totalElements());

            if (currentTrends.totalElements() <= 5) {
                log.info("Database contains sample seed data ({} items). Triggering initial live AI trend ingestion...", currentTrends.totalElements());
                IngestionResult result = ingestTrendsUseCase.ingestTrends();
                log.info("Initial live ingestion completed successfully! Total items ingested: {} (GitHub: {}, HuggingFace: {})",
                        result.totalIngested(), result.githubCount(), result.huggingFaceCount());
            } else {
                log.info("Database already populated with {} trends. Skipping initial boot ingestion.", currentTrends.totalElements());
            }
        } catch (Exception e) {
            log.error("Failed to run initial trend ingestion on application startup: {}", e.getMessage(), e);
        }
    }
}
