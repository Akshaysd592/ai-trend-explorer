package com.aitrend.trend.adapter.out.kafka;

import com.aitrend.trend.application.port.out.TrendEventPublisherPort;
import com.aitrend.trend.domain.model.Trend;
import com.aitrend.trend.infrastructure.kafka.event.TrendIngestedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Kafka producer outbound adapter that publishes trend lifecycle events
 * to Apache Kafka topics.
 */
@Component
public class KafkaTrendEventProducerAdapter implements TrendEventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(KafkaTrendEventProducerAdapter.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String trendIngestedTopic;

    public KafkaTrendEventProducerAdapter(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${app.kafka.topics.trend-ingested:ai.trends.ingested}") String trendIngestedTopic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.trendIngestedTopic = trendIngestedTopic;
    }

    @Override
    public void publishTrendIngestedEvent(Trend trend) {
        TrendIngestedEvent event = new TrendIngestedEvent(
                trend.getId(),
                trend.getTitle(),
                trend.getDescription(),
                trend.getRepositoryUrl(),
                trend.getSource() != null ? trend.getSource().name() : "UNKNOWN",
                trend.getStars(),
                trend.getForks(),
                trend.getLanguage(),
                trend.getTopics(),
                trend.getTrendScore(),
                Instant.now()
        );

        String messageKey = trend.getId() != null ? trend.getId().toString() : trend.getTitle();

        try {
            log.info("Publishing TrendIngestedEvent to Kafka topic '{}' with key '{}' for '{}'",
                    trendIngestedTopic, messageKey, trend.getTitle());

            kafkaTemplate.send(trendIngestedTopic, messageKey, event)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to deliver Kafka event for trend '{}': {}",
                                    trend.getTitle(), ex.getMessage());
                        } else {
                            log.debug("Delivered Kafka event for trend '{}' to partition {} with offset {}",
                                    trend.getTitle(),
                                    result.getRecordMetadata().partition(),
                                    result.getRecordMetadata().offset());
                        }
                    });
        } catch (Exception e) {
            log.error("Error dispatching Kafka message for trend '{}': {}", trend.getTitle(), e.getMessage());
        }
    }
}
