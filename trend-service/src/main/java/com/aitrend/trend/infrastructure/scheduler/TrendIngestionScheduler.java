package com.aitrend.trend.infrastructure.scheduler;

import com.aitrend.trend.application.port.in.IngestTrendsUseCase;
import com.aitrend.trend.application.port.in.IngestionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TrendIngestionScheduler {

    private static final Logger log = LoggerFactory.getLogger(TrendIngestionScheduler.class);
    private final IngestTrendsUseCase ingestTrendsUseCase;

    public TrendIngestionScheduler(IngestTrendsUseCase ingestTrendsUseCase) {
        this.ingestTrendsUseCase = ingestTrendsUseCase;
    }

    // Default cron: 0 0 0,12 * * * (Every 12 hours at 12:00 AM and 12:00 PM)
    @Scheduled(cron = "${trend.ingestion.cron:0 0 0,12 * * *}")
    public void scheduleIngestion() {
        log.info("Triggering scheduled 12-hour AI trend ingestion job...");
        try {
            IngestionResult result = ingestTrendsUseCase.ingestTrends();
            log.info("Scheduled ingestion finished successfully. Total ingested: {}", result.totalIngested());
        } catch (Exception e) {
            log.error("Error occurred during scheduled ingestion: {}", e.getMessage(), e);
        }
    }
}
