package com.aitrend.trend.adapter.in.web.dto;

import com.aitrend.trend.domain.model.SourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CreateTrendRequestDto(
    @NotBlank(message = "Title is required")
    String title,

    String description,
    String repositoryUrl,

    @NotNull(message = "Source is required")
    SourceType source,

    Integer stars,
    Integer forks,
    String language,
    List<String> topics,
    Double trendScore,
    String aiCategory,
    String aiSummary
) {}
