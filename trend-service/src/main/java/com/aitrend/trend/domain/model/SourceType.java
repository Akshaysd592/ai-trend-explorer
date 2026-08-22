package com.aitrend.trend.domain.model;

/**
 * Enum representing supported source platforms for trend ingestion.
 * Designed to be easily extensible for future platforms (e.g. PRODUCT_HUNT, REDDIT, KAGGLE).
 */
public enum SourceType {
    GITHUB,
    HUGGING_FACE
}

