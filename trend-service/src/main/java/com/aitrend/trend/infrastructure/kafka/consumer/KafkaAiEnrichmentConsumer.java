package com.aitrend.trend.infrastructure.kafka.consumer;

import com.aitrend.trend.application.port.out.AiEnrichmentPort;
import com.aitrend.trend.application.port.out.TrendRepositoryPort;
import com.aitrend.trend.domain.model.AiMetadata;
import com.aitrend.trend.domain.model.SourceType;
import com.aitrend.trend.domain.model.Trend;
import com.aitrend.trend.infrastructure.kafka.event.TrendIngestedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Kafka event listener that asynchronously consumes TrendIngestedEvents from topic
 * 'ai.trends.ingested', calls Google Gemini AI to summarize & categorize the trend,
 * and updates the database.
 */
@Component
public class KafkaAiEnrichmentConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaAiEnrichmentConsumer.class);

    private final AiEnrichmentPort aiEnrichmentPort;
    private final TrendRepositoryPort trendRepositoryPort;

    public KafkaAiEnrichmentConsumer(
            AiEnrichmentPort aiEnrichmentPort,
            TrendRepositoryPort trendRepositoryPort
    ) {
        this.aiEnrichmentPort = aiEnrichmentPort;
        this.trendRepositoryPort = trendRepositoryPort;
    }

    @KafkaListener(
            topics = "${app.kafka.topics.trend-ingested:ai.trends.ingested}",
            groupId = "${spring.kafka.consumer.group-id:trend-enrichment-group}"
    )
    public void handleTrendIngested(TrendIngestedEvent event) {
        log.info("Received Kafka TrendIngestedEvent for trend id: {}, title: '{}'",
                event.trendId(), event.title());

        try {
            SourceType source = SourceType.valueOf(event.source());
            Trend trend = new Trend(
                    event.trendId(),
                    event.title(),
                    event.description(),
                    event.repositoryUrl(),
                    source,
                    event.stars(),
                    event.forks(),
                    event.language(),
                    event.topics(),
                    event.trendScore(),
                    null,
                    null,
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );

            AiMetadata aiMetadata = aiEnrichmentPort.enrich(trend);

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
                    aiMetadata.category(),
                    aiMetadata.summary(),
                    trend.getCreatedAt(),
                    LocalDateTime.now()
            );

            trendRepositoryPort.save(enrichedTrend);
            log.info("Kafka consumer successfully AI-enriched trend id: {} (Category: '{}')",
                    trend.getId(), aiMetadata.category());

        } catch (Exception e) {
            log.error("Kafka consumer failed to process AI enrichment for trend id: {}: {}",
                    event.trendId(), e.getMessage(), e);
        }
    }
}
