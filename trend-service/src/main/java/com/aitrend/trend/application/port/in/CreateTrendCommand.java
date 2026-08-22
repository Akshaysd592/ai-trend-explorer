package com.aitrend.trend.application.port.in;

import com.aitrend.trend.domain.model.SourceType;
import java.util.List;

public record CreateTrendCommand(
    String title,
    String description,
    String repositoryUrl,
    SourceType source,
    Integer stars,
    Integer forks,
    String language,
    List<String> topics,
    Double trendScore,
    String aiCategory,
    String aiSummary
) {}
