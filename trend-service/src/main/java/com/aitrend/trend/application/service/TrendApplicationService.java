package com.aitrend.trend.application.service;

import com.aitrend.trend.application.port.in.CreateTrendCommand;
import com.aitrend.trend.application.port.in.GetTrendsQuery;
import com.aitrend.trend.application.port.in.PagedResult;
import com.aitrend.trend.application.port.in.TrendUseCase;
import com.aitrend.trend.application.port.out.TrendRepositoryPort;
import com.aitrend.trend.domain.exception.TrendNotFoundException;
import com.aitrend.trend.domain.model.Trend;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class TrendApplicationService implements TrendUseCase {

    private final TrendRepositoryPort trendRepositoryPort;

    public TrendApplicationService(TrendRepositoryPort trendRepositoryPort) {
        this.trendRepositoryPort = trendRepositoryPort;
    }

    /**
     * Cached paginated trends query.
     * Cache key includes all query parameters so each unique filter combination
     * gets its own cache entry. TTL is 5 minutes (configured in RedisConfig).
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(
        value = "trends",
        key = "#query.page() + '-' + #query.size() + '-' + #query.source() + '-' + #query.language() + '-' + #query.searchKeyword() + '-' + #query.sortBy() + '-' + #query.sortDirection()"
    )
    public PagedResult<Trend> getTrends(GetTrendsQuery query) {
        return trendRepositoryPort.findTrends(query);
    }

    @Override
    @Transactional(readOnly = true)
    public Trend getTrendById(Long id) {
        return trendRepositoryPort.findById(id)
                .orElseThrow(() -> new TrendNotFoundException(id));
    }

    /**
     * Evicts all "trends" cache entries when a new trend is manually created,
     * so the next GET reflects the latest data immediately.
     */
    @Override
    @CacheEvict(value = "trends", allEntries = true)
    public Trend createTrend(CreateTrendCommand command) {
        Trend newTrend = new Trend(
                null,
                command.title(),
                command.description(),
                command.repositoryUrl(),
                command.source(),
                command.stars(),
                command.forks(),
                command.language(),
                command.topics(),
                command.trendScore(),
                command.aiCategory(),
                command.aiSummary(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        return trendRepositoryPort.save(newTrend);
    }
}
