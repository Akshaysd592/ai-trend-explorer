package com.aitrend.trend.adapter.in.web.mapper;

import com.aitrend.trend.adapter.in.web.dto.CreateTrendRequestDto;
import com.aitrend.trend.adapter.in.web.dto.TrendResponseDto;
import com.aitrend.trend.application.port.in.CreateTrendCommand;
import com.aitrend.trend.domain.model.Trend;

public class TrendWebMapper {

    public static CreateTrendCommand toCommand(CreateTrendRequestDto dto) {
        return new CreateTrendCommand(
                dto.title(),
                dto.description(),
                dto.repositoryUrl(),
                dto.source(),
                dto.stars(),
                dto.forks(),
                dto.language(),
                dto.topics(),
                dto.trendScore(),
                dto.aiCategory(),
                dto.aiSummary()
        );
    }

    public static TrendResponseDto toResponseDto(Trend domain) {
        return new TrendResponseDto(
                domain.getId(),
                domain.getTitle(),
                domain.getDescription(),
                domain.getRepositoryUrl(),
                domain.getSource(),
                domain.getStars(),
                domain.getForks(),
                domain.getLanguage(),
                domain.getTopics(),
                domain.getTrendScore(),
                domain.getAiCategory(),
                domain.getAiSummary(),
                domain.getCreatedAt(),
                domain.getUpdatedAt()
        );
    }
}
