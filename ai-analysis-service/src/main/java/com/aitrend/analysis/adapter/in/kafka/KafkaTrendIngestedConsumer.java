package com.aitrend.analysis.adapter.in.kafka;

import com.aitrend.analysis.adapter.in.kafka.dto.TrendIngestedEvent;
import com.aitrend.analysis.application.port.in.AnalyzeTrendUseCase;
import com.aitrend.analysis.domain.model.TrendAnalysisRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaTrendIngestedConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaTrendIngestedConsumer.class);

    private final AnalyzeTrendUseCase analyzeTrendUseCase;

    public KafkaTrendIngestedConsumer(AnalyzeTrendUseCase analyzeTrendUseCase) {
        this.analyzeTrendUseCase = analyzeTrendUseCase;
    }

    @KafkaListener(
            topics = "${app.kafka.topics.trend-ingested:ai.trends.ingested}",
            groupId = "${spring.kafka.consumer.group-id:ai-analysis-group}"
    )
    public void handleTrendIngested(TrendIngestedEvent event) {
        log.info("ai-analysis-service consumed TrendIngestedEvent for trend id: {}, title: '{}'",
                event.trendId(), event.title());

        try {
            TrendAnalysisRequest request = new TrendAnalysisRequest(
                    event.trendId(),
                    event.title(),
                    event.description(),
                    event.repositoryUrl(),
                    event.language(),
                    event.topics(),
                    event.stars(),
                    event.forks()
            );

            analyzeTrendUseCase.processAndDispatchAnalysis(request);

        } catch (Exception e) {
            log.error("ai-analysis-service failed processing trend id: {}: {}",
                    event.trendId(), e.getMessage(), e);
        }
    }
}
