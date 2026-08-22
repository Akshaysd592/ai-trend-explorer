package com.aitrend.trend.adapter.in.web.dto;

import com.aitrend.trend.domain.model.SourceType;
import java.time.LocalDateTime;
import java.util.List;

public record TrendResponseDto(
    Long id,
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
    String aiSummary,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
