package com.aitrend.trend.application.port.out;

import com.aitrend.trend.domain.model.Trend;

/**
 * Outbound port for publishing trend lifecycle events to the event broker (Kafka).
 */
public interface TrendEventPublisherPort {

    /**
     * Publishes an event when a trend has been ingested and saved.
     *
     * @param trend the persisted Trend domain entity
     */
    void publishTrendIngestedEvent(Trend trend);
}
