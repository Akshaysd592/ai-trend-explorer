package com.aitrend.trend.domain.exception;

public class TrendNotFoundException extends RuntimeException {
    public TrendNotFoundException(Long id) {
        super("Trend not found with ID: " + id);
    }
}
