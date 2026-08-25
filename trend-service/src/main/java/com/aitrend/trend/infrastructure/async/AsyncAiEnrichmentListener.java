package com.aitrend.trend.infrastructure.async;

import com.aitrend.trend.application.event.IngestionCompletedEvent;
import com.aitrend.trend.application.port.out.AiEnrichmentPort;
import com.aitrend.trend.application.port.out.TrendRepositoryPort;
import com.aitrend.trend.domain.model.AiMetadata;
import com.aitrend.trend.domain.model.Trend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AsyncAiEnrichmentListener {

    private static final Logger log = LoggerFactory.getLogger(AsyncAiEnrichmentListener.class);

    private final AiEnrichmentPort aiEnrichmentPort;
    private final TrendRepositoryPort trendRepositoryPort;

    public AsyncAiEnrichmentListener(AiEnrichmentPort aiEnrichmentPort, TrendRepositoryPort trendRepositoryPort) {
        this.aiEnrichmentPort = aiEnrichmentPort;
        this.trendRepositoryPort = trendRepositoryPort;
    }

    @Async
    @EventListener
    public void handleIngestionCompleted(IngestionCompletedEvent event) {
        if (event.getTrends() == null || event.getTrends().isEmpty()) {
            return;
        }

        log.info("Starting background AI enrichment pipeline for {} trends...", event.getTrends().size());

        int enrichedCount = 0;
        for (Trend trend : event.getTrends()) {
            try {
                AiMetadata metadata = aiEnrichmentPort.enrich(trend);

                Trend enrichedTrend = new Trend(
                        trend.getId(),
                        trend.getTitle(),
                        trend.getDescription(),
                        trend.getRepositoryUrl(),
                        trend.getSource(),
                        trend.getStars(),
                        trend.getForks(),
                        trend.getLanguage(),
                        trend.getTopics(),
                        trend.getTrendScore(),
                        metadata.category(),
                        metadata.summary(),
                        trend.getCreatedAt(),
                        trend.getUpdatedAt()
                );

                trendRepositoryPort.save(enrichedTrend);
                enrichedCount++;

                // Rate limiting pause between Gemini API calls
                Thread.sleep(300);
            } catch (Exception e) {
                log.error("Failed background AI enrichment for '{}': {}", trend.getTitle(), e.getMessage());
            }
        }
        log.info("Background AI enrichment pipeline complete. Enriched {} items.", enrichedCount);
    }
}
