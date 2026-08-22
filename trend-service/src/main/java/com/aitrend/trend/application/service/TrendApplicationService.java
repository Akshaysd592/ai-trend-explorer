package com.aitrend.trend.application.service;

import com.aitrend.trend.application.port.in.CreateTrendCommand;
import com.aitrend.trend.application.port.in.GetTrendsQuery;
import com.aitrend.trend.application.port.in.PagedResult;
import com.aitrend.trend.application.port.in.TrendUseCase;
import com.aitrend.trend.application.port.out.TrendRepositoryPort;
import com.aitrend.trend.domain.exception.TrendNotFoundException;
import com.aitrend.trend.domain.model.Trend;
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

    @Override
    @Transactional(readOnly = true)
    public PagedResult<Trend> getTrends(GetTrendsQuery query) {
        return trendRepositoryPort.findTrends(query);
    }

    @Override
    @Transactional(readOnly = true)
    public Trend getTrendById(Long id) {
        return trendRepositoryPort.findById(id)
                .orElseThrow(() -> new TrendNotFoundException(id));
    }

    @Override
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
