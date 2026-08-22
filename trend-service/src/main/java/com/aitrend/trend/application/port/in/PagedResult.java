package com.aitrend.trend.application.port.in;

import java.util.List;

public record PagedResult<T>(
    List<T> content,
    int pageNumber,
    int pageSize,
    long totalElements,
    int totalPages,
    boolean last
) {}
