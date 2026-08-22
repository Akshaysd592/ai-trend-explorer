package com.aitrend.trend.application.port.out;

import com.aitrend.trend.domain.model.Trend;

import java.util.List;

public interface FetchGitHubTrendsPort {
    List<Trend> fetchTrendingRepositories();
}

