package com.aitrend.trend.application.port.in;

import com.aitrend.trend.domain.model.Trend;

public interface TrendUseCase {
    PagedResult<Trend> getTrends(GetTrendsQuery query);
    Trend getTrendById(Long id);
    Trend createTrend(CreateTrendCommand command);
    Trend updateAiMetadata(Long id, String aiCategory, String aiSummary);
}
