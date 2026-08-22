package com.aitrend.trend.application.port.in;

import com.aitrend.trend.domain.model.SourceType;

public record GetTrendsQuery(
    SourceType source,
    String language,
    String searchKeyword,
    int page,
    int size,
    String sortBy,
    String sortDirection
) {
    public GetTrendsQuery {
        if (page < 0) page = 0;
        if (size <= 0 || size > 100) size = 20;
        if (sortBy == null || sortBy.isBlank()) sortBy = "stars";
        if (sortDirection == null || sortDirection.isBlank()) sortDirection = "desc";
    }
}
