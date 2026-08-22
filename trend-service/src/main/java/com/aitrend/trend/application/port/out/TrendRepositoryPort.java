package com.aitrend.trend.application.port.out;

import com.aitrend.trend.application.port.in.GetTrendsQuery;
import com.aitrend.trend.application.port.in.PagedResult;
import com.aitrend.trend.domain.model.Trend;
import java.util.Optional;

public interface TrendRepositoryPort {
    PagedResult<Trend> findTrends(GetTrendsQuery query);
    Optional<Trend> findById(Long id);
    Trend save(Trend trend);
}
