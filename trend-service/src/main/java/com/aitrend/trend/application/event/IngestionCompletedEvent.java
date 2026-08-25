package com.aitrend.trend.application.event;

import com.aitrend.trend.domain.model.Trend;

import java.util.List;

public class IngestionCompletedEvent {

    private final List<Trend> trends;

    public IngestionCompletedEvent(List<Trend> trends) {
        this.trends = trends;
    }

    public List<Trend> getTrends() {
        return trends;
    }
}
