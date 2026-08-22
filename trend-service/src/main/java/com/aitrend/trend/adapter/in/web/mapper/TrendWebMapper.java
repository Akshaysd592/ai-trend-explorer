package com.aitrend.trend.adapter.in.web.mapper;

import com.aitrend.trend.application.port.in.CreateTrendCommand;
import com.aitrend.trend.domain.model.Trend;
import com.aitrend.trend.infrastructure.openapi.dto.CreateTrendRequestDto;
import com.aitrend.trend.infrastructure.openapi.dto.SourceType;
import com.aitrend.trend.infrastructure.openapi.dto.TrendResponseDto;

public class TrendWebMapper {

    public static CreateTrendCommand toCommand(CreateTrendRequestDto dto) {
        com.aitrend.trend.domain.model.SourceType source = dto.getSource() != null ?
                com.aitrend.trend.domain.model.SourceType.valueOf(dto.getSource().getValue()) : null;

        return new CreateTrendCommand(
                dto.getTitle(),
                dto.getDescription(),
                dto.getRepositoryUrl(),
                source,
                dto.getStars(),
                dto.getForks(),
                dto.getLanguage(),
                dto.getTopics(),
                dto.getTrendScore(),
                dto.getAiCategory(),
                dto.getAiSummary()
        );
    }

    public static TrendResponseDto toResponseDto(Trend domain) {
        return new TrendResponseDto()
                .id(domain.getId())
                .title(domain.getTitle())
                .description(domain.getDescription())
                .repositoryUrl(domain.getRepositoryUrl())
                .source(domain.getSource() != null ? SourceType.fromValue(domain.getSource().name()) : null)
                .stars(domain.getStars())
                .forks(domain.getForks())
                .language(domain.getLanguage())
                .topics(domain.getTopics())
                .trendScore(domain.getTrendScore())
                .aiCategory(domain.getAiCategory())
                .aiSummary(domain.getAiSummary());
    }
}
